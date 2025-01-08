package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductValidationDto;
import com.alexandrerocha.product.exceptions.ProductNotFoundException;
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

import static jakarta.servlet.http.HttpServletResponse.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService service;
    
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", service.getAllOrderedByPriceAsc());
        return "productListing";
    }
    
    @GetMapping("/register")
    public String showRegisterProductForm(Model model) {
        model.addAttribute("productDto", new ProductValidationDto());
        return "productRegistration";
    }
        
    @PostMapping("/register")
    public String registerProduct(@Valid @ModelAttribute("productDto") ProductValidationDto registrationDto,
                                  BindingResult bindingResult, Model model,
                                  HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            response.setStatus(SC_BAD_REQUEST);
            return "productRegistration";
        }
        service.saveProduct(registrationDto);
        var products = service.getAllOrderedByPriceAsc();

        response.setStatus(SC_CREATED);
        model.addAttribute("products", products);
        model.addAttribute("notification", "Produto cadastrado");
        return "productListing";
    }
    
    @GetMapping("/edit/{id}")
    public String showProductUpdatePage(@PathVariable long id, Model model) {
        var editingProduct = service.getProductForUpdate(id);
        model.addAttribute("editingProduct", editingProduct);
        return "productEditing";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable long id,
                                @Valid @ModelAttribute("editingProduct") ProductValidationDto updatingProductDto,
                                BindingResult bindingResult,
                                Model model, HttpServletResponse response) {
        
        // checks for product existence before handle validation
        service.getProduct(id); 
        
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            response.setStatus(SC_BAD_REQUEST);
            return "productEditing";
        }
        
        service.updateProduct(id, updatingProductDto);
        model.addAttribute("notification", "Produto atualizado");
        return "productEditing";
    }    
    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable long id, RedirectAttributes redirectAttributes){
        service.delete(id);
        redirectAttributes.addFlashAttribute("notification", "Produto deletado");
        return "redirect:/products";
    }
    
    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFound(ProductNotFoundException ex, Model model, HttpServletResponse response) {
        response.setStatus(SC_NOT_FOUND);
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
}
