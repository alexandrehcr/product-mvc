package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductDto;
import com.alexandrerocha.product.dto.ProductRegistrationDto;

public final class ProductMapping {

    private ProductMapping() {
    }

    public static Product mapToEntity(ProductRegistrationDto dto) {
       return new Product(
                null,
               dto.getName(),
               dto.getDescription(),
               dto.getPrice(),
               dto.getAvailable()
        );
    }
    
    public static ProductDto mapToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getIsAvailable());
    }
}
