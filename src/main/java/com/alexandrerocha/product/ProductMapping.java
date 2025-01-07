package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductDto;
import com.alexandrerocha.product.dto.ProductSubmissionDto;

public final class ProductMapping {

    private ProductMapping() {
    }

    public static Product mapToEntity(ProductSubmissionDto dto) {
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

    public static ProductSubmissionDto mapToProductSubmissionDto(Product product) {
        return new ProductSubmissionDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getIsAvailable());
    }
}
