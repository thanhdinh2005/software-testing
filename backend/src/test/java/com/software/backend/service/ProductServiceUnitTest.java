package com.software.backend.service;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.UpdateProductRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Product;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductMapper productMapper;

    private final Long EXISTING_ID = 1L;
    private Product existingEntity;
    private ProductResponse expectedResponse;

    @BeforeEach
    void setUp() {
        existingEntity = new Product(EXISTING_ID, "Old name", 5L, 100.0, "Old description", "ELECTRONICS");
        expectedResponse = new ProductResponse(EXISTING_ID, "New Name", 5L, 100.0, "Old Description", "ELECTRONICS");
    }

    @Test
    @DisplayName("TC_CREATE_1: Tao thanh cong san pham moi - Happy Path")
    void testCreateProductSuccess() throws BadRequestException {
        ProductRequest request = new ProductRequest(
                "Laptop ABC", 123.0, 10L, "", "ELECTRONICS"
        );

        ProductResponse expectedResponse = new ProductResponse(1L, "Laptop ABC", 10L, 123.0, "", "ELECTRONICS");

        Product entityToSave = new Product(null, "Laptop ABC", 10L, 123.0, "", "ELECTRONICS");
        Product savedEntity = new Product(1L, "Laptop ABC", 10L, 123.0, "", "ELECTRONICS");

        when(productMapper.toEntity(any(ProductRequest.class))).thenReturn(entityToSave);
        when(repository.save(any(Product.class))).thenReturn(savedEntity);
        when(productMapper.toResponse(any(Product.class))).thenReturn(expectedResponse);

        ProductResponse actualResponse = service.createProduct(request);

        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getPrice(), actualResponse.getPrice());

        verify(productMapper, times(1)).toEntity(any(ProductRequest.class));
        verify(repository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toResponse(any(Product.class));
    }

    @Test
    @DisplayName("TC_GET_1: Lay Product thanh cong theo ID")
    void testGetProductSuccess() {
        Long existingId = 1L;
        Product foundEntity = new Product(existingId,"Monitor Z", 10L, 300.0, "4K", "ELECTRONICS");
        ProductResponse expectedResponse = new ProductResponse(existingId, "Monitor Z", 10L, 300.0, "4K", "ELECTRONICS");

        when(repository.findById(existingId)).thenReturn(Optional.of(foundEntity));
        when(productMapper.toResponse(foundEntity)).thenReturn(expectedResponse);

        ProductResponse actualResponse = service.getById(existingId);

        assertNotNull(actualResponse, "Phản hồi không được null.");
        assertEquals(existingId, actualResponse.getId());
        assertEquals("Monitor Z", actualResponse.getName());

        verify(repository, times(1)).findById(existingId);
        verify(productMapper, times(1)).toResponse(foundEntity);
    }

    @Test
    @DisplayName("TC_GET_ALL_1: Lay tat ca Product co phan trang")
    void testGetAllProducts_WithPagination() {
        int page = 0;
        int size = 5;
        Long totalElements = 10L;
        int totalPages = 2;

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());

        Product product1 = new Product(1L, "Laptop A", 1L, 10.0, "", "E");
        Product product2 = new Product(2L, "Laptop B", 2L, 20.0, "", "E");
        List<Product> productList = List.of(product1, product2);

        Page<Product> productPage = new PageImpl<>(productList, pageable, totalElements);

        ProductResponse resp1 = new ProductResponse(1L, "Laptop A", 1L, 10.0, "", "E");
        ProductResponse resp2 = new ProductResponse(2L, "Laptop B", 2L, 20.0, "", "E");

        when(repository.findAll(pageable)).thenReturn(productPage);

        when(productMapper.toResponse(product1)).thenReturn(resp1);
        when(productMapper.toResponse(product2)).thenReturn(resp2);

        PageResponse<ProductResponse> actualPageResponse = service.getAllProducts(page, size);

        assertNotNull(actualPageResponse, "Phản hồi phân trang không được null.");

        assertEquals(2, actualPageResponse.getContent().size(), "Phải có 2 phần tử trong trang hiện tại.");
        assertEquals(totalElements.longValue(), actualPageResponse.getTotalElements(), "Tổng số phần tử phải là 10.");
        assertEquals(totalPages, actualPageResponse.getTotalPages(), "Tổng số trang phải là 2.");
        assertEquals(page, actualPageResponse.getNumber(), "Số trang phải là 0.");
        assertEquals(size, actualPageResponse.getSize(), "Kích thước trang phải là 5.");
        assertTrue(actualPageResponse.isFirst(), "Đây phải là trang đầu tiên.");
        assertFalse(actualPageResponse.isLast(), "Đây không phải là trang cuối cùng.");

        assertEquals("Laptop A", actualPageResponse.getContent().getFirst().getName());

        verify(repository, times(1)).findAll(pageable);
        verify(productMapper, times(2)).toResponse(any(Product.class));
    }

    
    @Test
    @DisplayName("TC_DEL_1: Xoa Product thanh cong")
    void testDeleteProductSuccess() {
        Long existingId = 1L;
        Product existingEntity = new Product(existingId, "Monitor Z", 10L, 300.0, "4K Monitor", "ELECTRONICS");

        when(repository.findById(existingId)).thenReturn(Optional.of(existingEntity));

        service.deleteProduct(existingId);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("TC_UPDATE_1: Cap nhat Product thanh cong - Chi thay doi ten")
    void testUpdateProductSuccess_ChangeNameOnly() throws BadRequestException {
        String newName = "New Name";
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.of(newName), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        Product updatedEntity = new Product(EXISTING_ID, newName, 5L, 100.0, "Old Description", "ELECTRONICS");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(Product.class))).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

        ProductResponse response = service.updateProductById(EXISTING_ID, request);

        assertNotNull(response);
        assertEquals(newName, response.getName());

        verify(repository, times(1)).findById(EXISTING_ID);
        verify(repository, times(1)).save(any(Product.class));
    }


}