package com.alexandrerocha.product;

import com.alexandrerocha.entity.Product;
import com.alexandrerocha.util.ProductMapper;
import com.alexandrerocha.entity.dto.ProductValidationDto;
import com.alexandrerocha.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository repository;
    
    final String INVALID_HUGE_NAME = "Huge name".repeat(12); // length = 108
    final String INVALID_HUGE_DESCRIPTION = "Huge Description".repeat(63); // length = 1008
    

    @Test
    void showProductsListing() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpectAll(
                        status().isOk(),
                        view().name("productListing"),
                        model().attributeExists("products"));
    }
    
    @Test
    void showRegisterProductForm() throws Exception {
        mockMvc.perform(get("/products/register"))
                .andExpectAll(
                        status().isOk(),
                        view().name("productRegistration"));
    }

    @Test
    void registerProduct_EmptyData_BadRequest() throws Exception {
        mockMvc.perform(
                        post("/products/register")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("name", "")
                                .param("description", "")
                                .param("price", ""))
                .andExpectAll(
                        status().isBadRequest(),
                        model().hasErrors());
    }


    @Test
    void registerProduct_InputTooLarge_BadRequest() throws Exception {
        mockMvc.perform(
                        post("/products/register")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("name", INVALID_HUGE_NAME)
                                .param("description", INVALID_HUGE_DESCRIPTION)
                                .param("price", "1"))
                .andExpectAll(
                        status().isBadRequest(),
                        model().hasErrors(),
                        model().errorCount(2));
    }

    @Test
    void registerProduct_ValidInputs() throws Exception {
        var validDto = new ProductValidationDto();
        validDto.setName("Test Name");
        validDto.setDescription("Test Description");
        validDto.setPrice(new BigDecimal("100.00"));
        validDto.setIsAvailable(true);

        mockMvc.perform(
                        post("/products/register")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("name", validDto.getName())
                                .param("description", validDto.getDescription())
                                .param("price", String.valueOf(validDto.getPrice()))
                                .param("isAvailable", String.valueOf(validDto.getIsAvailable())))
                .andExpectAll(
                        status().isCreated(),
                        model().hasNoErrors());
        
        var savedProduct = repository.findByName(validDto.getName())
                .orElseThrow(() -> new AssertionError("Product not found."));
        assertEquals(validDto.getName(), savedProduct.getName());
        assertEquals(validDto.getDescription(), savedProduct.getDescription());
        assertEquals(validDto.getPrice(), savedProduct.getPrice());
        assertEquals(validDto.getIsAvailable(), savedProduct.getIsAvailable());
    }
    
    @Nested
    class UpdateProductTests {
        
        Product saved;
        Long id;
        
        @BeforeEach
        void setup() {
            saved = repository.save(new Product(null, "Test Name", "Test description", new BigDecimal("123.00"), true));
            id = saved.getId();
        }
        
        @AfterEach()
        void cleanup() {
            repository.deleteById(id);
        }
        
        @Test
        void showProductUpdatePage() throws Exception {
            mockMvc.perform(get("/products/edit/{id}", id))
                    .andExpectAll(
                            status().isOk(),
                            view().name("productEditing"));
        }
        
        @Test
        void updateProduct_validInputs() throws Exception {
            var updatedProductDto = ProductMapper.mapToProductValidationDto(saved);
            updatedProductDto.setName("Updated Test Name");
            updatedProductDto.setDescription("Updated Description");
            updatedProductDto.setPrice(new BigDecimal("321.00"));
            updatedProductDto.setIsAvailable(false);

            mockMvc.perform(post("/products/edit/{id}", id)
                            .param("name", updatedProductDto.getName())
                            .param("description", updatedProductDto.getDescription())
                            .param("price", String.valueOf(updatedProductDto.getPrice()))
                            .param("isAvailable", String.valueOf(updatedProductDto.getIsAvailable())))
                    .andExpect(status().isOk());
            
            saved = repository.findById(id).orElseThrow(() -> new AssertionError("Product not found."));
            assertEquals(saved.getName(), updatedProductDto.getName());
            assertEquals(saved.getDescription(), updatedProductDto.getDescription());
            assertEquals(saved.getPrice(), updatedProductDto.getPrice());
            assertEquals(saved.getIsAvailable(), updatedProductDto.getIsAvailable());
        }
        
        @Test
        void updateProduct_InputTooLarge_BadRequest() throws Exception {
            var invalidProductDto = new ProductValidationDto();
            invalidProductDto.setName(INVALID_HUGE_NAME);
            invalidProductDto.setDescription(INVALID_HUGE_DESCRIPTION);

            mockMvc.perform(post("/products/edit/{id}", id)
                            .param("name", invalidProductDto.getName())
                            .param("description", invalidProductDto.getDescription()))
                    .andExpectAll(
                            status().isBadRequest(),
                            model().hasErrors());
        }

        @Test
        void updateProduct_EmptyData_BadRequest() throws Exception {
            mockMvc.perform(post("/products/edit/{id}", id)
                            .param("name", "")
                            .param("description", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(model().hasErrors());
        }
    }
    
    @Nested
    class DeleteProductTests {
        Product saved;
        Long id;

        @BeforeEach
        void setup() {
            saved = repository.save(new Product(null, "Test Name", "Test description", new BigDecimal("123.00"), true));
            id = saved.getId();
        }
        
        @Test
        void deleteProduct_ProductExists_RedirectAndNotify() throws Exception {
            mockMvc.perform(get("/products/delete/{id}", id))
                    .andExpectAll(
                            status().isFound(),
                            flash().attributeExists("notification"),
                            redirectedUrl("/products")
                    );
            
            assertTrue(repository.findById(id).isEmpty());
        }
        
        @Test
        void deleteProduct_ProductDoesntExist_NotFound() throws Exception {
            mockMvc.perform(get("/products/delete/{id}", Long.MAX_VALUE))
                    .andExpect(status().isNotFound());
        }
    }
        
    // When the program is running, `GET /non-defined-url` returns the 404.html view and 404 status.
    @Test
    @Disabled("Fails probably due to Spring Boot Test error handler behavior; revisit later.")
    void show404() throws Exception {
        mockMvc.perform(get("/non-defined-url"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
    }
}