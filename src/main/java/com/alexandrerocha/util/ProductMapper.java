package com.alexandrerocha.util;

import com.alexandrerocha.entity.Product;
import com.alexandrerocha.entity.dto.ProductDto;
import com.alexandrerocha.entity.dto.ProductValidationDto;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product mapToEntity(ProductValidationDto dto) {
        return new Product(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getIsAvailable()
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

    public static ProductValidationDto mapToProductValidationDto(Product product) {
        return new ProductValidationDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getIsAvailable());
    }
}
