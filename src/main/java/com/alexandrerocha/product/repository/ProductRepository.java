package com.alexandrerocha.product.repository;

import com.alexandrerocha.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOrderByPriceAsc();
}
