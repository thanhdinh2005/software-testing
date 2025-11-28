package com.software.backend.controller;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.SearchProductRequest;
import com.software.backend.dto.request.UpdateProductRequest;
import com.software.backend.dto.response.ApiResponse;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public PageResponse<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        ProductResponse res = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody @Valid ProductRequest request) throws BadRequestException {
        ProductResponse res = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(res));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest request
    ) throws BadRequestException {
        ProductResponse res = productService.updateProductById(id, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    @PostMapping("/search")
    public PageResponse<ProductResponse> searchProducts(
            @RequestBody SearchProductRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.searchProduct(request, page, size);
    }
}
