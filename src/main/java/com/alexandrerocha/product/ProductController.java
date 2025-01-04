package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductRegistrationDto;
import com.alexandrerocha.product.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductRepository repository;
    
    @GetMapping("/catalogue")
    public String listProducts(Model model) {
        model.addAttribute("products", repository.findByOrderByPriceAsc());
        return "productCatalogue";
    }
    
    @GetMapping("/register")
    public String showRegisterProductForm(Model model) {
        model.addAttribute("productDto", new ProductRegistrationDto());
        return "productRegistration";
    }
    
    @PostMapping("/register")
    public String registerProduct(@Valid ProductRegistrationDto productRegistrationDto, Model model) {
        var product = ProductMapping.mapToEntity(productRegistrationDto);
        repository.save(product);
        
        var products = repository.findByOrderByPriceAsc()
                .stream()
                .map(ProductMapping::mapToDto)
                .toList();
        model.addAttribute("products", products);
        
        return "productCatalogue";
    }
}
