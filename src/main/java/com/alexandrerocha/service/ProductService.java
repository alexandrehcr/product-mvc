package com.alexandrerocha.service;

import com.alexandrerocha.entity.dto.ProductDto;
import com.alexandrerocha.entity.dto.ProductValidationDto;
import com.alexandrerocha.exceptions.ProductNotFoundException;
import com.alexandrerocha.repository.ProductRepository;
import com.alexandrerocha.util.ProductMapper;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;
    
    
    public Page<ProductDto> getPage(Pageable pageable) throws BadRequestException {
         if (pageable.getPageSize() > 50)
             throw new BadRequestException("Invalid page size.");
         
        var productsDtoPage =  repository.findAll(pageable)
                .map(ProductMapper::mapToDto);
        
        if (pageable.getPageNumber() >= productsDtoPage.getTotalPages())
            throw new BadRequestException("Invalid page number.");

        return productsDtoPage;
    }
    
    public ProductDto saveProduct(ProductValidationDto registrationDto) {
        var product = ProductMapper.mapToEntity(registrationDto);
        product = repository.save(product);
        return ProductMapper.mapToDto(product);
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
