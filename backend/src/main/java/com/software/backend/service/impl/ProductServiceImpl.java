package com.software.backend.service.impl;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    @Override
    public List<ProductResponse> getAllProducts() {
        return List.of();
    }

    @Override
    public ProductResponse getById(Long id) {
        return null;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        return null;
    }

    @Override
    public ProductResponse updateProductById(Long id, ProductRequest request) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }
}
