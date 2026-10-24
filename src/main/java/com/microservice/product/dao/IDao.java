package com.microservice.product.dao;

import java.util.List;

/**
 * Contrat CRUD générique imposé par le TP. Le type T permet de réutiliser ce
 * contrat avec d'autres entités sans dupliquer les opérations communes.
 */
public interface IDao<T> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    T findById(Long id);
    List<T> findAll();
}
