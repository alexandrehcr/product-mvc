package com.alexandrerocha.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public class ProductValidationDto {
    private Long id;

    @NotBlank(message = "O nome do produto não pode ser vazio.")
    @Length(min = 2, max = 100, message = "O nome deve conter de 2 a 100 caracteres.")
    private String name;

    @NotBlank(message = "A descrição do produto não pode ser vazia.")
    @Length(max = 1000, message = "A descrição deve conter até 1000 caracteres.")
    private String description;

    @Min(value = 0, message = "O preço do produto não pode ser negativo.")
    @NotNull(message = "Defina um preço para o produto.")
    private BigDecimal price;

    private boolean isAvailable;


    public ProductValidationDto() {
    }

    public ProductValidationDto(Long id, String name, String description, BigDecimal price, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        this.isAvailable = available;
    }
}
