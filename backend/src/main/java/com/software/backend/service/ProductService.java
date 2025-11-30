package com.software.backend.service;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.SearchProductRequest;
import com.software.backend.dto.request.UpdateProductRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Category;
import com.software.backend.entity.Product;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.ProductRepository;
import com.software.backend.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
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

    public ProductResponse createProduct(ProductRequest request) {
        validateProductRequest(request);
        validateCategory(request.categoryName());
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    /**
     * Updates a product partially using fields provided in request.
     *
     * @param id ID of the product to update
     * @param request update fields (optional values)
     * @return updated product details
     * @throws ResourceNotFoundException if product does not exist
     * @throws BadRequestException if request contains invalid fields
     */
    public ProductResponse updateProductById(Long id, UpdateProductRequest request) {
        if (isEmptyUpdate(request)) {
            throw new BadRequestException("Update request cannot be empty");
        }

        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        applyUpdates(currentProduct, request);

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

    private void applyUpdates(Product product, UpdateProductRequest request) {

        request.name().ifPresent(name -> {
            if (name.isBlank() || name.length() < 3 || name.length() > 100)
                throw new IllegalArgumentException("Name cannot be blank and must be between 3 - 100 characters");
            product.setName(name);
        });

        request.description().ifPresent(desc -> {
            if (desc.length() > 500)
                throw new IllegalArgumentException("Description must be valid");
            product.setDescription(desc);
        });

        request.price().ifPresent(price -> {
            if (price < 0 || price > 999999999)
                throw new IllegalArgumentException("Invalid price");
            product.setPrice(price);
        });

        request.quantity().ifPresent(quantity -> {
            if (quantity < 0 || quantity > 99999)
                throw new IllegalArgumentException("Invalid quantity");
            product.setQuantity(quantity);
        });

        request.categoryName().ifPresent(cate -> {
            if (!Category.exists(cate))
                throw new IllegalArgumentException("Category does not exist: " + cate);
            product.setCategoryName(cate);
        });
    }

    private void validateCategory(String category) {
        if (!Category.exists(category)) {
            throw new BadRequestException("Category does not exist: " + category);
        }
    }

    private boolean isEmptyUpdate(UpdateProductRequest request) {
        return request.name().isEmpty()
                && request.description().isEmpty()
                && request.price().isEmpty()
                && request.quantity().isEmpty()
                && request.categoryName().isEmpty();
    }

    private void validateProductRequest(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Product request cannot be null");
        }

        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        String name = request.name().trim();
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Product name must be between 3 - 100 characters");
        }

        if (request.description().length() > 500) {
            throw new IllegalArgumentException("Description must be <= 500 characters");
        }

        if (request.price() == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (request.price() <= 0 || request.price() > 999_999_999) {
            throw new IllegalArgumentException("Price must be > 0 and <= 999,999,999");
        }

        if (request.quantity() == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (request.quantity() < 0 || request.quantity() > 99_999) {
            throw new IllegalArgumentException("Quantity must be >= 0 and <= 99,999");
        }
    }
}
