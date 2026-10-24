package com.microservice.product.dao;

import com.microservice.product.entity.Product;
import com.microservice.product.entity.ProductStatus;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implémentation Hibernate de l'accès aux produits. Cette classe ne contient
 * aucune règle métier : elle traduit uniquement les demandes en requêtes HQL.
 */
@Repository
public class ProductDaoImpl implements ProductDao {
    private final SessionFactory sessionFactory;

    /** La SessionFactory est fournie automatiquement par Spring. */
    public ProductDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Product product) {
        sessionFactory.getCurrentSession().save(product);
    }

    @Override
    public void update(Product product) {
        sessionFactory.getCurrentSession().update(product);
    }

    @Override
    public void delete(Product product) {
        sessionFactory.getCurrentSession().delete(product);
    }

    @Override
    public Product findById(Long id) {
        return sessionFactory.getCurrentSession().get(Product.class, id);
    }

    @Override
    public List<Product> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Product", Product.class)
                .getResultList();
    }

    @Override
    public boolean existsBySku(String sku, Long excludedProductId) {
        String hql = "SELECT COUNT(p.id) FROM Product p WHERE p.sku = :sku";
        if (excludedProductId != null) {
            hql += " AND p.id <> :excludedId";
        }
        Query<Long> query = sessionFactory.getCurrentSession()
                .createQuery(hql, Long.class)
                .setParameter("sku", sku);
        if (excludedProductId != null) {
            query.setParameter("excludedId", excludedProductId);
        }
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(p.id) FROM Product p WHERE p.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Product> search(String name, ProductStatus status) {
        StringBuilder hql = new StringBuilder("FROM Product p WHERE 1 = 1");
        if (name != null && !name.trim().isEmpty()) {
            hql.append(" AND LOWER(p.name) LIKE :name");
        }
        if (status != null) {
            hql.append(" AND p.status = :status");
        }
        hql.append(" ORDER BY p.name");

        Query<Product> query = sessionFactory.getCurrentSession()
                .createQuery(hql.toString(), Product.class);
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toLowerCase() + "%");
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

    @Override
    public double calculateInventoryValue() {
        Double value = sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT SUM(p.price * p.stockQuantity) FROM Product p "
                                + "WHERE p.status <> :status",
                        Double.class)
                .setParameter("status", ProductStatus.DISCONTINUED)
                .uniqueResult();
        return value == null ? 0.0 : value;
    }
}
