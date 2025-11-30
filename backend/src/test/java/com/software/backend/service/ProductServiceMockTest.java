package com.software.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.SearchProductRequest;
import com.software.backend.dto.request.UpdateProductRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Product;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceMockTest {
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
    @DisplayName("TC_CREATE_1: Create a new product success - Happy Path")
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
    @DisplayName("TC_FAIL_3: Create Product Failure - Category Name Does Not Exist")
    void testCreateProductFailure_InvalidCategory() {
        String invalidCategoryName = "FASHION";
        ProductRequest request = new ProductRequest(
                "New Shirt",
                50.0,
                10L,
                "Stylish shirt",
                invalidCategoryName
        );

        Exception exception = assertThrows(BadRequestException.class, () -> service.createProduct(request));

        String expectedMessagePart = "Category does not exist: ";
        assertTrue(exception.getMessage().contains(expectedMessagePart),
                "Thông báo lỗi phải chỉ ra Category không hợp lệ.");

        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("TC_CREATE_2: Create failed - Invalid name")
    void testCreateProductInvalidName() {
        ProductRequest request = new ProductRequest(
                "AB",
                100.0,
                10L,
                "Good",
                "ELECTRONICS"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createProduct(request)
        );

        assertEquals("Product name must be between 3 - 100 characters", ex.getMessage());

        verify(repository, never()).save(any());
        verify(productMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("TC_CREATE_3: Create failed - Invalid price")
    void testCreateProductInvalidPrice() {
        ProductRequest request = new ProductRequest(
                "Laptop",
                -10.0,
                10L,
                "Desc",
                "ELECTRONICS"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createProduct(request)
        );

        assertEquals("Price must be > 0 and <= 999,999,999", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("TC_CREATE_4: Create failed - Invalid quantity")
    void testCreateProductInvalidQuantity() {
        ProductRequest request = new ProductRequest(
                "Laptop",
                100.0,
                -5L,
                "Desc",
                "ELECTRONICS"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createProduct(request)
        );

        assertEquals("Quantity must be >= 0 and <= 99,999", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("TC_CREATE_5: Create failed - Description too long")
    void testCreateProductInvalidDescription() {
        String longDesc = "A".repeat(600);

        ProductRequest request = new ProductRequest(
                "Laptop",
                100.0,
                10L,
                longDesc,
                "ELECTRONICS"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createProduct(request)
        );

        assertEquals("Description must be <= 500 characters", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("TC_GET_1")
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
    @DisplayName("TC_GET_2: Get Product Failure - Not Found by ID")
    void testGetProductByIdFailure_NotFound() {
        Long nonExistentId = 99L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.getById(nonExistentId);
        });

        String expectedMessage = "Product not found with id: " + nonExistentId;
        assertTrue(exception.getMessage().contains(expectedMessage),
                "Thông báo lỗi phải khớp với ResourceNotFoundException.");

        verify(repository, times(1)).findById(nonExistentId);
        verify(productMapper, times(0)).toResponse(any());
    }

    @Test
    @DisplayName("TC_GET_ALL_1: Get All Products with Pagination and Descending Sort")
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
    @DisplayName("TC_DEL_1: Delete Product Success")
    void testDeleteProductSuccess() {
        Long existingId = 1L;
        Product existingEntity = new Product(existingId, "Monitor Z", 10L, 300.0, "4K Monitor", "ELECTRONICS");

        when(repository.findById(existingId)).thenReturn(Optional.of(existingEntity));

        service.deleteProduct(existingId);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("TC_DEL_2: Delete Product Failure - Not Found ID")
    void testDeleteProductByIdFailure_NotFound() {
        Long nonExistentId = 99L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteProduct(nonExistentId);
        });

        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, times(0)).deleteById(anyLong());
    }

    @Test
    @DisplayName("TC_SEARCH_1: Search Products with Multiple Filters and Pagination")
    void testSearchProducts_WithFiltersAndPagination() {
        int page = 0;
        int size = 10;
        Long totalElements = 1L;

        SearchProductRequest request = new SearchProductRequest(
                "Laptop", 1L, 10L, 500.0, 1500.0, null, "ELECTRONICS"
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());

        Product product1 = new Product(1L, "Laptop ABC", 5L, 1200.0, "", "ELECTRONICS");
        List<Product> productList = List.of(product1);

        Page<Product> productPage = new PageImpl<>(productList, pageable, totalElements);

        ProductResponse resp1 = new ProductResponse(1L, "Laptop ABC", 5L,1200.0 , "", "ELECTRONICS");

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

        when(productMapper.toResponse(product1)).thenReturn(resp1);

        PageResponse<ProductResponse> actualPageResponse = service.searchProduct(request, page, size);

        assertNotNull(actualPageResponse, "Phản hồi phân trang không được null.");

        assertEquals(1, actualPageResponse.getContent().size(), "Phải có 1 phần tử trong trang hiện tại.");
        assertEquals(totalElements.longValue(), actualPageResponse.getTotalElements(), "Tổng số phần tử phải là 5.");
        assertEquals("Laptop ABC", actualPageResponse.getContent().getFirst().getName());

        verify(repository, times(1)).findAll(any(Specification.class), eq(pageable));

        verify(productMapper, times(1)).toResponse(any(Product.class));
    }

    @Test
    @DisplayName("TC1: Update Product Success - Change Name Only")
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

    @Test
    @DisplayName("TC2: Update Failure - Request is Empty")
    void testUpdateProductFailure_RequestIsEmpty() {
        UpdateProductRequest emptyRequest = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        assertThrows(BadRequestException.class, () -> {
            service.updateProductById(EXISTING_ID, emptyRequest);
        });

        verify(repository, times(0)).findById(anyLong());
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("TC3: Update Failure - Product ID Not Found")
    void testUpdateProductFailure_NotFound() {
        Long nonExistentId = 99L;
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.of("ABC"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.updateProductById(nonExistentId, request);
        });

        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("TC4: Validation Failure - Invalid Name (Blank)")
    void testUpdateProductFailure_BlankName() {
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.of("   "), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateProductById(EXISTING_ID, request);
        });

        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("TC5: Validation Failure - Invalid Description (Too long)")
    void testUpdateProductFailure_DescriptionTooLong() {
        String longDesc = "a".repeat(501);
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(longDesc), Optional.empty()
        );
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateProductById(EXISTING_ID, request);
        });
    }

    @Test
    @DisplayName("TC6: Validation Failure - Invalid Price (Negative)")
    void testUpdateProductFailure_NegativePrice() {
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.of(-10.0), Optional.empty(), Optional.empty()
        );
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateProductById(EXISTING_ID, request);
        });
    }

    @Test
    @DisplayName("TC7: Validation Failure - Invalid Category (Does Not Exist)")
    void testUpdateProductFailure_InvalidCategory() {
        String invalidCategory = "FASHION";
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(invalidCategory)
        );
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateProductById(EXISTING_ID, request);
        });
    }
/////Lai-----------------------------------------------------
   @Test
   @DisplayName("Null request should throw IllegalArgumentException")
   void nullRequest_shouldThrowException() {
       ProductRequest request = null;
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
    assertThat(ex.getMessage()).isEqualTo("Product request cannot be null");

   }

   @Test
   @DisplayName("Blank product name should throw IllegalArgumentException")
   void blankName_shouldThrowException() {
       ProductRequest request = new ProductRequest("   ", 100.0, 1L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Product name cannot be blank");
   }

   @Test
   @DisplayName("Procuct name length less than 3 should throw IllegalArgumentException")
   void shortName_shouldThrowException() {
       ProductRequest request = new ProductRequest("AB", 100.0, 1L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Product name must be between 3 - 100 characters");
   }

   @Test
   @DisplayName("Description length more than 500 should throw IllegalArgumentException")
   void longDescription_shouldThrowException() {
       String longDesc = "a".repeat(501);
       ProductRequest request = new ProductRequest("Valid Name", 100.0, 1L, longDesc, "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Description must be <= 500 characters");

   }

   @Test
   @DisplayName("Price be null should throw IllegalArgumentException")
   void nullPrice_shouldThrowException() {
       ProductRequest request = new ProductRequest("Valid Name", null, 1L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Price cannot be null");
   }

   @Test
   @DisplayName("Price less than equal 0 should throw IllegalArgumentException")
   void nonPositivePrice_shouldThrowException() {
       ProductRequest request = new ProductRequest("Valid Name", 0.0, 1L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Price must be > 0 and <= 999,999,999");
   }

   @Test
   @DisplayName("Price more than 999,999,999 should throw IllegalArgumentException")
   void excessivePrice_shouldThrowException() {
       ProductRequest request = new ProductRequest("Valid Name", 1_000_000_000.0, 1L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Price must be > 0 and <= 999,999,999");
   }

   @Test
   @DisplayName("Quantity be null should throw IllegalArgumentException")
   void nullQuantity_shouldThrowException() {
       ProductRequest request = new ProductRequest("Valid Name", 100.0, null, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Quantity cannot be null");
   }

   @Test
   @DisplayName("Quantity less than 0 should throw IllegalArgumentException")
   void negativeQuantity_shouldThrowException() {
       ProductRequest request = new ProductRequest("Valid Name", 100.0, -1L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Quantity must be >= 0 and <= 99,999");
   }

   @Test
   @DisplayName("Quantity more than 99,999 should throw IllegalArgumentException")
   void excessiveQuantity_shouldThrowException() {
       ProductRequest request = new ProductRequest("Valid Name", 100.0, 100_000L, "Description", "Category");
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
               () -> {
                   service.createProduct(request);
               });
       assertThat(ex.getMessage()).isEqualTo("Quantity must be >= 0 and <= 99,999");
   }

   @Test
@DisplayName("Update product - blank name should throw exception")
void testUpdateProduct_blankNameThrowsException() {
    UpdateProductRequest request = new UpdateProductRequest(
            Optional.of(""), Optional.empty(), Optional.of(150.0), Optional.empty(), Optional.empty()
    );

    when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> service.updateProductById(EXISTING_ID, request));

    assertThat(ex.getMessage()).isEqualTo("Name cannot be blank and must be between 3 - 100 characters");
}

@Test
@DisplayName("Update product - name too short should throw exception")
void testUpdateProduct_shortNameThrowsException() {
    UpdateProductRequest request = new UpdateProductRequest(
            Optional.of("AB"), Optional.empty(), Optional.of(150.0), Optional.empty(), Optional.empty()
    );

    when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> service.updateProductById(EXISTING_ID, request));

    assertThat(ex.getMessage()).isEqualTo("Name cannot be blank and must be between 3 - 100 characters");
}


@Test
@DisplayName("Update product - name too long should throw exception")
void testUpdateProduct_longNameThrowsException() {  
    String longName = "A".repeat(101);
    UpdateProductRequest request = new UpdateProductRequest(
            Optional.of(longName), Optional.empty(), Optional.of(150.0), Optional.empty(), Optional.empty()
    );

    when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> service.updateProductById(EXISTING_ID, request));
            assertThat(ex.getMessage()).isEqualTo("Name cannot be blank and must be between 3 - 100 characters");
}
@Test
@DisplayName("Update product - description too long should throw exception")
void testUpdateProduct_descriptionTooLongThrowsException() {  
    String longDescription = "A".repeat(501);
    UpdateProductRequest request = new UpdateProductRequest(
            Optional.empty(), Optional.empty(), Optional.of(150.0), Optional.of(longDescription), Optional.empty()
    );

    when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> service.updateProductById(EXISTING_ID, request));
            assertThat(ex.getMessage()).isEqualTo("Description must be valid");
}
@Test
    @DisplayName("Update product - negative price should throw exception")
    void testUpdateProduct_invalidPriceIsNegativeThrowsException() {
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.of(-10.0), Optional.empty(), Optional.empty()
        );

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateProductById(EXISTING_ID, request));

        assertThat(ex.getMessage()).isEqualTo("Invalid price");
    }

@Test
    @DisplayName("Update product - price too high should throw exception")
    void testUpdateProduct_invalidPriceIsTooHighThrowsException() {
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.of(1_000_000_000.0), Optional.empty(), Optional.empty()
        );

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateProductById(EXISTING_ID, request));

        assertThat(ex.getMessage()).isEqualTo("Invalid price");
    }
@Test
    @DisplayName("Update product - negative quantity should throw exception")
    void testUpdateProduct_invalidQuantityIsNegativeThrowsException() {
         UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.of(-1L), Optional.empty(), Optional.empty(), Optional.empty()
        );
    
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateProductById(EXISTING_ID, request));

        assertThat(ex.getMessage()).isEqualTo("Invalid quantity");

    }

@Test
    @DisplayName("Update product - quantity too high should throw exception")
    void testUpdate_invalidQuantityTooHighThrowsException(){
         UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.of(1_000_000_000L), Optional.empty(), Optional.empty(), Optional.empty()
        );
    
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateProductById(EXISTING_ID, request));

        assertThat(ex.getMessage()).isEqualTo("Invalid quantity");

    }
@Test
    @DisplayName("Update product - invalid category should throw exception")
    void testUpdate_invalidCategoryThrowsException() {
            UpdateProductRequest request = new UpdateProductRequest(
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("INVALID_CATEGORY")
            );
        
            when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateProductById(EXISTING_ID, request));

        assertThat(ex.getMessage()).isEqualTo("Category does not exist: " + "INVALID_CATEGORY");
    }


    @Test
    @DisplayName("Update product - request cannot be empty should throw exception")
    void testUpdate_emptyRequestThrowsException() {
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.updateProductById(EXISTING_ID, request));
        assertThat(ex.getMessage()).isEqualTo("Update request cannot be empty");

}

    @Test
    @DisplayName("Update product - product not found should throw exception")
    void testUpdate_productNotFoundThrowsException() {
        UpdateProductRequest request = new UpdateProductRequest(
            Optional.of("ValidName"), Optional.empty(), Optional.of(200.0), Optional.empty(), Optional.empty()
    );
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.updateProductById(EXISTING_ID, request));
        assertThat(ex.getMessage()).isEqualTo("Product not found with id: " + EXISTING_ID);

}
@Test
@DisplayName("Update product - all valid fields should update successfully")
void testUpdateProduct_allValidFieldsUpdatesSuccessfully() throws BadRequestException {
    String newName = "New Name";
    UpdateProductRequest request = new UpdateProductRequest(
            Optional.of(newName), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
    );

    Product updatedEntity = new Product(EXISTING_ID, newName, 5L, 100.0, "Old Description", "ELECTRONICS");
    ProductResponse expectedResponse = new ProductResponse(EXISTING_ID, newName, 5L, 100.0, "Old Description", "ELECTRONICS");

    when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
    when(repository.save(any(Product.class))).thenReturn(updatedEntity);
    when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

    ProductResponse response = service.updateProductById(EXISTING_ID, request);

    assertNotNull(response);
    assertEquals(EXISTING_ID, response.getId());
    assertEquals(newName, response.getName());
    assertEquals(5L, response.getQuantity());
    assertEquals(100.0, response.getPrice());
    assertEquals("Old Description", response.getDescription());
    assertEquals("ELECTRONICS", response.getCategoryName());

    verify(repository, times(1)).findById(EXISTING_ID);
    verify(repository, times(1)).save(any(Product.class));
}
    @Test
    @DisplayName("Update product - update name successfully")
    void testUpdateProduct_updateNameSuccessfully() throws BadRequestException {
        String newName = "Updated Name";
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.of(newName), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        Product updatedEntity = new Product(EXISTING_ID, newName, 5L, 100.0, "Old Description", "ELECTRONICS");
        ProductResponse expectedResponse = new ProductResponse(EXISTING_ID, newName, 5L, 100.0, "Old Description", "ELECTRONICS");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(Product.class))).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

        ProductResponse response = service.updateProductById(EXISTING_ID, request);

        assertNotNull(response);
        assertEquals(EXISTING_ID, response.getId());
        assertEquals(newName, response.getName());

        verify(repository, times(1)).findById(EXISTING_ID);
        verify(repository, times(1)).save(any(Product.class));
    }
    @Test
    @DisplayName("Update product - update description successfully")
    void testUpdateProduct_updateDescriptionSuccessfully() throws BadRequestException {
        String newDescription = "Updated Description";
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(newDescription), Optional.empty()
        );

        Product updatedEntity = new Product(EXISTING_ID, "Old Name", 5L, 100.0, newDescription, "ELECTRONICS");
        ProductResponse expectedResponse = new ProductResponse(EXISTING_ID, "Old Name", 5L, 100.0, newDescription, "ELECTRONICS");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(Product.class))).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

        ProductResponse response = service.updateProductById(EXISTING_ID, request);

        assertNotNull(response);
        assertEquals(EXISTING_ID, response.getId());
        assertEquals(newDescription, response.getDescription());

        verify(repository, times(1)).findById(EXISTING_ID);
        verify(repository, times(1)).save(any(Product.class));
    }
    @Test
    @DisplayName("Update product - update price successfully")
    void testUpdateProduct_updatePriceSuccessfully() throws BadRequestException {
        Double newPrice = 250.0;
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.empty(), Optional.of(newPrice), Optional.empty(), Optional.empty()
        );

        Product updatedEntity = new Product(EXISTING_ID, "Old Name", 5L, newPrice, "Old Description", "ELECTRONICS");
        ProductResponse expectedResponse = new ProductResponse(EXISTING_ID, "Old Name", 5L, newPrice, "Old Description", "ELECTRONICS");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(Product.class))).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);
        ProductResponse response = service.updateProductById(EXISTING_ID, request);

        assertNotNull(response);
        assertEquals(EXISTING_ID, response.getId());
        assertEquals(newPrice, response.getPrice());

        verify(repository, times(1)).findById(EXISTING_ID);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product - update quantity successfully")
    void testUpdateProduct_updateQuantitySuccessfully() throws BadRequestException {
        Long newQuantity = 10L;
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.empty(), Optional.of(newQuantity), Optional.empty(), Optional.empty(), Optional.empty()
        );

        Product updatedEntity = new Product(EXISTING_ID, "Old Name", newQuantity, 100.0, "Old Description", "ELECTRONICS");
        ProductResponse expectedResponse = new ProductResponse(EXISTING_ID, "Old Name", newQuantity, 100.0, "Old Description", "ELECTRONICS");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(Product.class))).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

        ProductResponse response = service.updateProductById(EXISTING_ID, request);

        assertNotNull(response);
        assertEquals(EXISTING_ID, response.getId());
        assertEquals(newQuantity, response.getQuantity());

        verify(repository, times(1)).findById(EXISTING_ID);
        verify(repository, times(1)).save(any(Product.class));
    }

   
}