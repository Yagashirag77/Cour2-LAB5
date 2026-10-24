package com.microservice.product;

import com.microservice.product.config.AppConfig;
import com.microservice.product.entity.Product;
import com.microservice.product.entity.ProductStatus;
import com.microservice.product.service.ProductService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Démonstration du catalogue avec des produits sur le thème de la Formule 1 :
 * création, sortie de stock, recherche et calcul de la valeur du stock.
 */
public class MainApp {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService productService = context.getBean(ProductService.class);

            Product ferrariCap = new Product(
                    "FER-CAP-16", "Casquette Scuderia Ferrari", 449.90, 12);
            Product landoNorrisModel = new Product(
                    "MCL-LN-04", "Miniature McLaren de Lando Norris", 799.00, 4);
            productService.addProduct(ferrariCap);
            productService.addProduct(landoNorrisModel);

            productService.removeStock(ferrariCap.getId(), 2);

            System.out.println("Produits disponibles : "
                    + productService.searchProducts(null, ProductStatus.AVAILABLE).size());
            System.out.printf("Valeur totale du stock : %.2f MAD%n",
                    productService.getInventoryValue());
        }
    }
}
