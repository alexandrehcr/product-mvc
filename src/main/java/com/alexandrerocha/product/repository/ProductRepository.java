package com.alexandrerocha.product.repository;

import com.alexandrerocha.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOrderByPriceAsc();
    Optional<Product> findByName(String name);
}
