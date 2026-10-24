package com.microservice.product;

import com.microservice.product.config.AppConfig;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Vérification minimale de la connexion et du démarrage du conteneur Spring. */
public class TestHibernate {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            SessionFactory sessionFactory = context.getBean(SessionFactory.class);
            System.out.println("Contexte Spring initialisé avec succès !");
            System.out.println("SessionFactory ouverte : " + sessionFactory.isOpen());
        }
    }
}
