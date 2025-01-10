package com.alexandrerocha.controller;

import com.alexandrerocha.entity.dto.ProductDto;
import com.alexandrerocha.entity.dto.ProductValidationDto;
import com.alexandrerocha.exceptions.ProductNotFoundException;
import com.alexandrerocha.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static jakarta.servlet.http.HttpServletResponse.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService service;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
    
    @GetMapping
    public String listProducts(
            @PageableDefault(sort = "price", direction = Sort.Direction.ASC, size = 10, page = 1) 
            Pageable pageable, 
            Model model) throws BadRequestException {
        
        // For the user it doesn't make sense to have 0-based indexed pages.
        var productsPage = service.getPage(pageable.previousOrFirst()); // adjust page # to 0-based
        addPageDataToModel(model, productsPage);
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
                                  HttpServletResponse response) throws BadRequestException {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            response.setStatus(SC_BAD_REQUEST);
            return "productRegistration";
        }
        var createdProduct = service.saveProduct(registrationDto);
        
        var productsPage = service.getPage(PageRequest.of(0, 10, Sort.by("price")));
        addPageDataToModel(model, productsPage);
        model.addAttribute("notification", "Produto cadastrado com ID " + createdProduct.id());
        
        response.setStatus(SC_CREATED);
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
    public String deleteProduct(@PathVariable long id, RedirectAttributes redirectAttributes) {
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

    private void addPageDataToModel(Model model, Page<ProductDto> page) {
        model.addAttribute("currPage", page.getNumber() + 1); // adjust page # to 1-based
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("products", page);
    }
}
