package com.software.backend.service;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.SearchProductRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Product;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.ProductRepository;
import com.software.backend.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toResponse)
                .toList();

        return new PageResponse<>(
                productResponses,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    public ProductResponse getById(Long id) {
        return productMapper.toResponse(productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id)));
    }

    public ProductResponse createProduct(ProductRequest request) throws BadRequestException {
        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Product name already exists");
        }
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    public ProductResponse updateProductById(Long id, ProductRequest request) throws BadRequestException {
        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Product name already exists");
        }
        Product newProduct = productMapper.toEntity(request);

        currentProduct.setName(newProduct.getName());
        currentProduct.setCategoryName(newProduct.getCategoryName());
        currentProduct.setPrice(newProduct.getPrice());
        currentProduct.setQuantity(newProduct.getQuantity());

        return productMapper.toResponse(productRepository.save(currentProduct));
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.deleteById(id);
    }

    public PageResponse<ProductResponse> searchProduct(SearchProductRequest request, int page, int size) {
        Specification<Product> specification = Specification.allOf(
                ProductSpecification.hasName(request.name()),
                ProductSpecification.quantityGreaterThan(request.quantityMin()),
                ProductSpecification.quantityLessThan(request.quantityMax()),
                ProductSpecification.priceGreaterThan(request.priceMin()),
                ProductSpecification.priceLessThan(request.priceMax()),
                ProductSpecification.hasCategory(request.categoryName())
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        Page<Product> products = productRepository.findAll(specification, pageable);
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toResponse)
                .toList();

        return new PageResponse<>(
                productResponses,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }
}
