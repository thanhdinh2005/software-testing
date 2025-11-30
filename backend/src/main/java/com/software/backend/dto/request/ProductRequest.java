package com.software.backend.dto.request;

import jakarta.validation.constraints.*;


public record ProductRequest(

        @NotNull(message = "Product name cannot be null")
        @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
        String name,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @DecimalMax(value = "999999999.0", inclusive = true, message = "Price must be <= 999,999,999")
        Double price,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 0, message = "Quantity must be >= 0")
        @Max(value = 99999, message = "Quantity must be <= 99,999")
        Long quantity,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Category name cannot be empty")
        String categoryName
) {}
