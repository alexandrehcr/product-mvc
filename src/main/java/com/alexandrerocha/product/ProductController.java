package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductSubmissionDto;
import com.alexandrerocha.product.exceptions.ProductNotFoundException;
import com.alexandrerocha.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    ProductRepository repository;
    
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", repository.findByOrderByPriceAsc());
        return "productListing";
    }
    
    @GetMapping("/register")
    public String showRegisterProductForm(Model model) {
        model.addAttribute("productDto", new ProductSubmissionDto());
        return "productRegistration";
    }
        
    @PostMapping("/register")
    public String registerProduct(@Valid @ModelAttribute("productDto") ProductSubmissionDto registrationDto,
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

        response.setStatus(201);
        model.addAttribute("products", products);
        model.addAttribute("notification", "Produto cadastrado");
        return "productListing";
    }
    
    @GetMapping("/edit/{id}")
    public String showProductUpdatePage(@PathVariable long id, Model model) {
        var editingProduct = repository.findById(id)
                .map(ProductMapping::mapToProductSubmissionDto)
                .orElseThrow(ProductNotFoundException::new);
        
        model.addAttribute("editingProduct", editingProduct);
        return "productEditing";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable long id,
                                @Valid @ModelAttribute("editingProduct") ProductSubmissionDto updatingProductDto,
                                BindingResult bindingResult,
                                Model model, HttpServletResponse response) {
        var product = repository.findById(id).orElseThrow(ProductNotFoundException::new);
        
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            response.setStatus(400);
            return "productEditing";
        }
        
        product = ProductMapping.mapToEntity(updatingProductDto);
        repository.save(product);
        model.addAttribute("notification", "Produto atualizado");
        return "productEditing";
    }    
    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable long id, RedirectAttributes redirectAttributes){
        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("notification", "Produto deletado");
        return "redirect:/products";
    }
    
    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFound(ProductNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
}
