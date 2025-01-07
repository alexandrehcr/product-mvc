package com.alexandrerocha.product;

import com.alexandrerocha.product.dto.ProductSubmissionDto;
import com.alexandrerocha.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository repository;


    @Test
    void listProducts() throws Exception {
        mockMvc.perform(get("/products/catalogue"))
                .andExpect(status().isOk())
                .andExpect(view().name("productCatalogue"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void showRegisterProductForm() throws Exception {
        mockMvc.perform(get("/products/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("productRegistration"));
    }

    @Test
    void registerProductBadRequestWithEmptyData() throws Exception {
        mockMvc.perform(
                        post("/products/register")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("name", "")
                                .param("description", "")
                                .param("price", ""))
                .andExpect(status().isBadRequest())
                .andExpect(model().hasErrors());
    }


    @Test
    void registerProductBadRequestWithDataBeyondLimit() throws Exception {
        String hugeName = "ABCDE".repeat(21); // length = 105
        String hugeDescription = "HugeDescription".repeat(67); // length = 1005
        
        mockMvc.perform(
                        post("/products/register")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("name", hugeName)
                                .param("description", hugeDescription)
                                .param("price", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2));
    }
    
    

    @Test
    void registerProductCreated() throws Exception {
        var validDto = new ProductSubmissionDto();
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
                .andExpect(status().isCreated())
                .andExpect(model().hasNoErrors())
                .andDo(print());

        var savedProduct = repository.findByName(validDto.getName())
                .orElseThrow(() -> new AssertionError("Product not found."));
        assertEquals(validDto.getName(), savedProduct.getName());
        assertEquals(validDto.getDescription(), savedProduct.getDescription());
        assertEquals(validDto.getPrice(), savedProduct.getPrice());
        assertEquals(validDto.getIsAvailable(), savedProduct.getIsAvailable());
    }
}