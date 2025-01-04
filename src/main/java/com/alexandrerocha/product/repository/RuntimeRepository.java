package com.alexandrerocha.product.repository;

import com.alexandrerocha.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class RuntimeRepository {
    private Set<Product> products = new HashSet<>(
            List.of(
                    new Product(1L, "Smart TV", "See the world as you never did", new BigDecimal("299.00"), true),
                    new Product(2L, "Smartphone", "Take the best photos", new BigDecimal("999.90"), true),
                    new Product(3L, "Laptop", "Incredible fast, small and elegant", new BigDecimal("1699.99"), false),
                    new Product(4L, "Freezer", "It's really cold inside", new BigDecimal("549.50"), true)));
    
    public List<Product> findAllOrderByPriceAsc() {
        var sortedList = new ArrayList<>(products);
        sortedList.sort(Comparator.comparing(Product::getPrice));
        return List.copyOf(products);
    }

    public Product save(Product product) {
        products.add(product);
        return product;
    }
    
    public long count() {
        return products.size();
    }
}
