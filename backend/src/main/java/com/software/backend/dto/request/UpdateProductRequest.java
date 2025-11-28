package com.software.backend.dto.request;

import java.util.Optional;

public record UpdateProductRequest (
        Optional<String> name,
        Optional<Long> quantity,
        Optional<Double> price,
        Optional<String> description,
        Optional<String> categoryName
){
}
