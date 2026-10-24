package com.microservice.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Assemble la source de données et la SessionFactory Hibernate à partir du
 * fichier application.properties.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class HibernateConfig {
    private final Environment environment;

    public HibernateConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        // DriverManagerDataSource est suffisant pour ce TP sans serveur applicatif.
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(requiredProperty("jdbc.driver"));
        dataSource.setUrl(requiredProperty("jdbc.url"));
        dataSource.setUsername(requiredProperty("jdbc.username"));
        dataSource.setPassword(requiredProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // Hibernate découvre automatiquement toutes les classes annotées @Entity.
        sessionFactory.setPackagesToScan("com.microservice.product.entity");

        Properties properties = new Properties();
        properties.put("hibernate.dialect", requiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", requiredProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", requiredProperty("hibernate.hbm2ddl.auto"));
        sessionFactory.setHibernateProperties(properties);
        return sessionFactory;
    }

    private String requiredProperty(String key) {
        return environment.getRequiredProperty(key);
    }
}
