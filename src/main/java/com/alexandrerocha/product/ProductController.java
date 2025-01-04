package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductRegistrationDto;
import com.alexandrerocha.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
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
    public String registerProduct(@Valid @ModelAttribute("productDto") 
                                      ProductRegistrationDto registrationDto,
                                  BindingResult bindingResult, Model model,
                                  HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            response.setStatus(400);
            return "productRegistration";
        }
        
        var product = ProductMapping.mapToEntity(registrationDto);
        repository.save(product);
        
        var products = repository.findByOrderByPriceAsc()
                .stream()
                .map(ProductMapping::mapToDto)
                .toList();
        
        model.addAttribute("products", products);
        
        response.setStatus(201);
        return "productCatalogue";
    }
}
