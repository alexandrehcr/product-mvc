package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductDto;
import com.alexandrerocha.product.dto.ProductValidationDto;

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
