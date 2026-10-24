package com.microservice.product.service;

import com.microservice.product.dao.ProductDao;
import com.microservice.product.entity.Product;
import com.microservice.product.entity.ProductStatus;
import com.microservice.product.exception.DuplicateSkuException;
import com.microservice.product.exception.InsufficientStockException;
import com.microservice.product.exception.InvalidProductStateException;
import com.microservice.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Implémentation des règles du catalogue informatique. Les transactions sont
 * placées ici afin qu'une opération métier complète réussisse ou soit annulée.
 */
@Service
public class ProductServiceImpl implements ProductService {
    private static final String SKU_PATTERN = "[A-Z0-9-]{3,20}";
    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    @Transactional
    public void addProduct(Product product) {
        normalizeAndValidate(product);
        ensureSkuIsAvailable(product.getSku(), null);
        synchronizeStatus(product);
        productDao.save(product);
    }

    @Override
    @Transactional
    public void updateProduct(Product product) {
        normalizeAndValidate(product);
        if (product.getId() == null || !productDao.existsById(product.getId())) {
            throw new ProductNotFoundException(product.getId());
        }
        ensureSkuIsAvailable(product.getSku(), product.getId());
        synchronizeStatus(product);
        productDao.update(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findExisting(id);
        productDao.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return findExisting(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    @Override
    @Transactional
    public void addStock(Long productId, int quantity) {
        requirePositiveQuantity(quantity);
        Product product = findExisting(productId);
        ensureProductIsActive(product);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        synchronizeStatus(product);
        // L'entité est gérée par Hibernate : le dirty checking persiste ce changement.
    }

    @Override
    @Transactional
    public void removeStock(Long productId, int quantity) {
        requirePositiveQuantity(quantity);
        Product product = findExisting(productId);
        ensureProductIsActive(product);
        if (quantity > product.getStockQuantity()) {
            throw new InsufficientStockException(quantity, product.getStockQuantity());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        synchronizeStatus(product);
    }

    @Override
    @Transactional
    public void discontinueProduct(Long productId) {
        Product product = findExisting(productId);
        product.setStatus(ProductStatus.DISCONTINUED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String name, ProductStatus status) {
        return productDao.search(name, status);
    }

    @Override
    @Transactional(readOnly = true)
    public double getInventoryValue() {
        return productDao.calculateInventoryValue();
    }

    /** Nettoie les données saisies puis contrôle les invariants d'un produit. */
    private void normalizeAndValidate(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être nul");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        product.setName(product.getName().trim());
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Le prix doit être strictement positif");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas être négatif");
        }
        if (product.getSku() == null) {
            throw new IllegalArgumentException("La référence SKU est obligatoire");
        }
        product.setSku(product.getSku().trim().toUpperCase(Locale.ROOT));
        if (!product.getSku().matches(SKU_PATTERN)) {
            throw new IllegalArgumentException(
                    "Le SKU doit contenir 3 à 20 lettres, chiffres ou tirets");
        }
    }

    private Product findExisting(Long id) {
        if (id == null) {
            throw new ProductNotFoundException(null);
        }
        Product product = productDao.findById(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }

    private void ensureSkuIsAvailable(String sku, Long currentProductId) {
        if (productDao.existsBySku(sku, currentProductId)) {
            throw new DuplicateSkuException(sku);
        }
    }

    private void requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive");
        }
    }

    private void ensureProductIsActive(Product product) {
        if (product.getStatus() == ProductStatus.DISCONTINUED) {
            throw new InvalidProductStateException(
                    "Un produit retiré du catalogue ne peut plus changer de stock");
        }
    }

    /** Le statut disponible/épuisé est toujours dérivé du stock réel. */
    private void synchronizeStatus(Product product) {
        product.setStatus(product.getStockQuantity() == 0
                ? ProductStatus.OUT_OF_STOCK
                : ProductStatus.AVAILABLE);
    }
}
