package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductDto;
import com.alexandrerocha.product.dto.ProductValidationDto;
import com.alexandrerocha.product.exceptions.ProductNotFoundException;
import com.alexandrerocha.product.repository.ProductRepository;
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
