package com.software.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.UpdateProductRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.service.ProductService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc(addFilters = false) // bỏ qua security filter
@SpringBootTest
class ProductControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ProductService productService;


        @Test
        @DisplayName("GET /api/products - Lay danh sach tat ca san pham")
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
        @DisplayName("GET /api/products/{id} - Lay san pham theo id")
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
        @DisplayName("POST /api/products - Tao san pham moi")
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
        @DisplayName("PUT /api/products/{id} - Cap nhat san pham thanh cong")
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
        @DisplayName("DELETE /api/products/{id} - Xoa san pham thanh cong")
        void delete_success() throws Exception {
        // Xóa sản phẩm thành công
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(productService).deleteProduct(1L);
        }

}