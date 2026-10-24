package com.microservice.product.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Point d'entrée de la configuration Spring. Le component scanning découvre
 * les configurations, services et DAO sous com.microservice.product.
 */
@Configuration
@ComponentScan(basePackages = "com.microservice.product")
public class AppConfig {
}
