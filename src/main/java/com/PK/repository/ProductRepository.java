package com.PK.repository;

import com.PK.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory_id(int id);

    List<Product> findByNameContainingIgnoreCase(String name);
}
