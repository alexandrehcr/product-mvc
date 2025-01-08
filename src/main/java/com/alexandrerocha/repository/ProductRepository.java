package com.alexandrerocha.repository;

import com.alexandrerocha.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findByPriceAsc();
    Optional<Product> findByName(String name);
}
