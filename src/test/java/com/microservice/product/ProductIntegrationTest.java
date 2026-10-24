package com.microservice.product;

import com.microservice.product.config.AppConfig;
import com.microservice.product.entity.Product;
import com.microservice.product.entity.ProductStatus;
import com.microservice.product.exception.DuplicateSkuException;
import com.microservice.product.exception.InsufficientStockException;
import com.microservice.product.exception.InvalidProductStateException;
import com.microservice.product.exception.ProductNotFoundException;
import com.microservice.product.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests d'intégration : Spring, Hibernate et les règles métier fonctionnent
 * ensemble sur une base H2 recréée pour chaque test.
 */
class ProductIntegrationTest {
    private AnnotationConfigApplicationContext context;
    private ProductService productService;

    @BeforeEach
    void startContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        productService = context.getBean(ProductService.class);
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void performsTheRequiredCrudOperations() {
        Product mouse = new Product("MOU-WL-01", "Souris sans fil", 229.90, 8);
        productService.addProduct(mouse);

        assertNotNull(mouse.getId());
        assertEquals(1, productService.getAllProducts().size());

        mouse.setPrice(219.90);
        productService.updateProduct(mouse);
        assertEquals(219.90, productService.getProductById(mouse.getId()).getPrice(), 0.001);

        productService.deleteProduct(mouse.getId());
        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(mouse.getId()));
    }

    @Test
    void stockMovementsAutomaticallyChangeTheStatus() {
        Product headset = new Product("AUD-PRO-02", "Casque audio professionnel", 650.0, 2);
        productService.addProduct(headset);

        productService.removeStock(headset.getId(), 2);
        assertEquals(ProductStatus.OUT_OF_STOCK,
                productService.getProductById(headset.getId()).getStatus());

        productService.addStock(headset.getId(), 5);
        Product restocked = productService.getProductById(headset.getId());
        assertEquals(5, restocked.getStockQuantity());
        assertEquals(ProductStatus.AVAILABLE, restocked.getStatus());
    }

    @Test
    void refusesInsufficientStockAndKeepsTheOriginalQuantity() {
        Product screen = new Product("SCR-4K-03", "Écran 4K", 3499.0, 3);
        productService.addProduct(screen);

        assertThrows(InsufficientStockException.class,
                () -> productService.removeStock(screen.getId(), 4));
        assertEquals(3, productService.getProductById(screen.getId()).getStockQuantity());
    }

    @Test
    void normalizesSkuAndRejectsDuplicates() {
        productService.addProduct(new Product(" cam-hd-01 ", "Webcam HD", 450.0, 4));

        assertThrows(DuplicateSkuException.class,
                () -> productService.addProduct(
                        new Product("CAM-HD-01", "Autre webcam", 500.0, 2)));
    }

    @Test
    void discontinuedProductCannotReceiveStock() {
        Product printer = new Product("PRN-LAS-04", "Imprimante laser", 1850.0, 1);
        productService.addProduct(printer);
        productService.discontinueProduct(printer.getId());

        assertThrows(InvalidProductStateException.class,
                () -> productService.addStock(printer.getId(), 2));
        assertEquals(ProductStatus.DISCONTINUED,
                productService.getProductById(printer.getId()).getStatus());
    }

    @Test
    void filtersProductsAndCalculatesInventoryValue() {
        productService.addProduct(new Product("KEY-MEC-05", "Clavier mécanique", 800.0, 2));
        productService.addProduct(new Product("KEY-MEM-06", "Clavier membrane", 200.0, 0));
        productService.addProduct(new Product("CAM-FHD-07", "Webcam Full HD", 400.0, 3));

        List<Product> availableKeyboards =
                productService.searchProducts("clavier", ProductStatus.AVAILABLE);

        assertEquals(1, availableKeyboards.size());
        assertEquals("KEY-MEC-05", availableKeyboards.get(0).getSku());
        assertEquals(2800.0, productService.getInventoryValue(), 0.001);
    }

    @Test
    void rejectsInvalidInputLimits() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(
                        new Product("X", "Produit", 10.0, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(
                        new Product("VALID-01", "Produit", 0.0, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(
                        new Product("VALID-02", "Produit", 10.0, -1)));
    }
}
