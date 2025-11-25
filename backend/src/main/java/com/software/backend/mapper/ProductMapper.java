package com.software.backend.mapper;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductRequest request){
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .categoryName(request.getCategoryName())
                .build();
    }

    public ProductResponse toResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryName(product.getCategoryName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}

