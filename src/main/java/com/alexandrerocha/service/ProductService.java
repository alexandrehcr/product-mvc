package com.alexandrerocha.service;

import com.alexandrerocha.entity.Product;
import com.alexandrerocha.entity.dto.ProductDto;
import com.alexandrerocha.entity.dto.ProductValidationDto;
import com.alexandrerocha.exceptions.ProductNotFoundException;
import com.alexandrerocha.repository.ProductRepository;
import com.alexandrerocha.util.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;
    
    
    public List<ProductDto> getAllOrderedByPriceAsc() {
        return repository.findByPriceAsc()
                .stream()
                .map(ProductMapper::mapToDto)
                .toList();
    }

    public Product saveProduct(ProductValidationDto registrationDto) {
        var product = ProductMapper.mapToEntity(registrationDto);
        return repository.save(product);
    }

    public ProductValidationDto getProductForUpdate(long id) {
        return repository.findById(id)
                .map(ProductMapper::mapToProductValidationDto)
                .orElseThrow(ProductNotFoundException::new);
    }
    
    public ProductDto updateProduct(long id, ProductValidationDto updatingProductDto) {
        var existingProduct = repository.findById(id).orElseThrow(ProductNotFoundException::new);
        existingProduct = ProductMapper.mapToEntity(updatingProductDto);
        var updatedProduct = repository.save(existingProduct);
        return ProductMapper.mapToDto(updatedProduct);
    }

    public ProductDto getProduct(long id) {
        return repository.findById(id)
                .map(ProductMapper::mapToDto)
                .orElseThrow(ProductNotFoundException::new);
    }

    public void delete(long id) {
        var product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));
        repository.delete(product);
    }
}
