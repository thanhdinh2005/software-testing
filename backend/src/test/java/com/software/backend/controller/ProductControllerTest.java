package com.software.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.UpdateProductRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.service.ProductService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ProductService productService;

        @Test
        @DisplayName("GET /api/products - should return paginated list of products")
        void getAllProducts_success() throws Exception {
        // Tạo dữ liệu giả lập gồm 1 sản phẩm
        PageResponse<ProductResponse> pageResponse =
                new PageResponse<>(List.of(new ProductResponse(1L, "Laptop", 10L, 1000.0, "Dien tu", "Electronic")),
                        0, 10, 1, 1, true, true);

        // Giả lập service trả về danh sách sản phẩm
        when(productService.getAllProducts(0, 10)).thenReturn(pageResponse);

        // Gửi request và kiểm tra phản hồi JSON
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

        // Đảm bảo service được gọi đúng
        verify(productService).getAllProducts(0, 10);
        }

        @Test
        @DisplayName("GET /api/products/{id} - should return 404 if product not found")
        void getById_notFound() throws Exception {
        // Giả lập service ném lỗi khi không tìm thấy sản phẩm
        when(productService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService).getById(99L);
        }

        @Test
        @DisplayName("GET /api/products/{id} - should return product by id")
        void getById_success() throws Exception {
        // Giả lập service trả về sản phẩm theo id
        ProductResponse response = new ProductResponse(1L, "Laptop", 10L, 1000.0, "Dien tu", "Electronic");
        when(productService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Laptop"));

        verify(productService).getById(1L);
        }

        

        @Test
        @DisplayName("POST /api/products - should return 400 if request violates validation rules")
        void create_badRequest_invalidInput() throws Exception {
        // Dữ liệu không hợp lệ: price <= 0 → vi phạm @DecimalMin
        ProductRequest badRequest = new ProductRequest("Laii", -50.0, 10L, "Dien tu", "Electronic");

        // Không cần mock service vì validation chặn trước
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Price must be greater than 0"));
                // Không cần verify service vì validation chặn trước khi gọi service
                        verify(productService, Mockito.never()).createProduct(any(ProductRequest.class));
                }

        @Test
        @DisplayName("POST /api/products - should create new product")
        void create_success() throws Exception {
        // Tạo sản phẩm hợp lệ
        ProductRequest request = new ProductRequest("Laptop", 1000.0, 10L, "Dien tu", "Electronic");
        ProductResponse response = new ProductResponse(1L, "Laptop", 10L, 1000.0, "Dien tu", "Electronic");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Laptop"));

        verify(productService).createProduct(any(ProductRequest.class));
        }


        @Test
        @DisplayName("PUT /api/products/{id} - should return 404 if product to update is not found")
        void update_notFound() throws Exception {
        // Giả lập không tìm thấy sản phẩm để cập nhật
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.of("Laptop Pro"), Optional.of(10L), Optional.of(1200.0),
                Optional.of("Dien tu"), Optional.of("Electronic"));

        when(productService.updateProductById(eq(999L), any(UpdateProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));

        verify(productService).updateProductById(eq(999L), any(UpdateProductRequest.class));
        }

        @Test
        @DisplayName("PUT /api/products/{id} - should update product successfully")
        void update_success() throws Exception {
        // Cập nhật sản phẩm thành công
        UpdateProductRequest request = new UpdateProductRequest(
                Optional.of("Laptop Pro"), Optional.of(10L), Optional.of(1200.0),
                Optional.of("Dien tu"), Optional.of("Electronic"));

        ProductResponse response = new ProductResponse(1L, "Laptop Pro", 10L, 1200.0, "Dien tu", "Electronic");

        when(productService.updateProductById(eq(1L), any(UpdateProductRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Laptop Pro"));

        verify(productService).updateProductById(eq(1L), any(UpdateProductRequest.class));
        }

        @Test
        @DisplayName("DELETE /api/products/{id} - should return 404 if product to delete is not found")
        void delete_notFound() throws Exception {
        // Giả lập không tìm thấy sản phẩm để xóa
        Mockito.doThrow(new ResourceNotFoundException("Product not found with id: 123"))
                .when(productService).deleteProduct(123L);

        mockMvc.perform(delete("/api/products/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: 123"));

        verify(productService).deleteProduct(123L);
        }

        @Test
        @DisplayName("DELETE /api/products/{id} - should delete product successfully")
        void delete_success() throws Exception {
        // Xóa sản phẩm thành công
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(productService).deleteProduct(1L);
        }
//-------------------------------
@Test
@DisplayName("POST /api/products - should return 400 if name is blank")
void create_badRequest_blankName() throws Exception {
    ProductRequest badRequest = new ProductRequest("", 1000.0, 10L, "Dien tu", "Electronic");

    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Product name must be between 3 and 100 characters"));

    verify(productService, Mockito.never()).createProduct(any(ProductRequest.class));
}

@Test
@DisplayName("POST /api/products - should return 400 if quantity is negative")
void create_badRequest_negativeQuantity() throws Exception {
    ProductRequest badRequest = new ProductRequest("Laptop", 1000.0, -5L, "Dien tu", "Electronic");

    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Quantity must be >= 0"));

    verify(productService, Mockito.never()).createProduct(any(ProductRequest.class));
}

@Test
@DisplayName("POST /api/products - should return 400 if categoryName is blank")
void create_badRequest_blankCategory() throws Exception {
    ProductRequest badRequest = new ProductRequest("Laptop", 1000.0, 10L, "Dien tu", "");

    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Category name cannot be empty"));

    verify(productService, Mockito.never()).createProduct(any(ProductRequest.class));
}

@Test
@DisplayName("POST /api/products/search - should return paginated search results")
void searchProducts_success() throws Exception {
    // Tạo dữ liệu giả lập gồm 1 sản phẩm
    PageResponse<ProductResponse> pageResponse =
            new PageResponse<>(List.of(new ProductResponse(1L, "Laptop", 10L, 1000.0, "Dien tu", "Electronic")),
                    0, 10, 1, 1, true, true);

    // Giả lập service trả về danh sách sản phẩm tìm kiếm
    when(productService.searchProduct(any(), eq(0), eq(10))).thenReturn(pageResponse);

    // Tạo request tìm kiếm
    String searchRequestJson = """
            {
                "name": "Laptop",
                "minPrice": 500.0,
                "maxPrice": 1500.0,
                "categoryName": "Electronic"
            }
            """;

    // Gửi request và kiểm tra phản hồi JSON
    mockMvc.perform(post("/api/products/search")
                    .contentType("application/json")
                    .content(searchRequestJson)
                    .param("page", "0")
                    .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Laptop"));

    // Đảm bảo service được gọi đúng
    verify(productService).searchProduct(any(), eq(0), eq(10));

}

}