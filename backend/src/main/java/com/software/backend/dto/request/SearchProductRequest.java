package com.software.backend.dto.request;

public record SearchProductRequest (
        String name,
        Long quantityMin,
        Long quantityMax,
        Double priceMin,
        Double priceMax,
        Double price,
        String categoryName
) {
}
