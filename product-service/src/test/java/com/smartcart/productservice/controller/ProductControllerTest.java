package com.smartcart.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.productservice.entity.Product;
import com.smartcart.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addProduct() throws Exception {
        Product product = Product.builder().name("Laptop").price(1000.0).build();
        Product savedProduct = Product.builder().id(1L).name("Laptop").price(1000.0).build();

        when(productService.addProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllProducts() throws Exception {
        Product p = Product.builder().id(1L).name("Laptop").build();
        when(productService.getAllProducts()).thenReturn(Arrays.asList(p));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void deductStock() throws Exception {
        mockMvc.perform(put("/api/products/1/deduct-stock").param("quantity", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct() throws Exception {
        Product product = Product.builder().name("Updated").build();
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductById() throws Exception {
        Product p = Product.builder().id(1L).name("Laptop").build();
        when(productService.getProductById(1L)).thenReturn(p);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void searchProducts() throws Exception {
        Product p = Product.builder().id(1L).name("Laptop").build();
        when(productService.searchProducts("Lap")).thenReturn(Arrays.asList(p));

        mockMvc.perform(get("/api/products/search").param("keyword", "Lap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void filterByCategory() throws Exception {
        Product p = Product.builder().id(1L).category("Tech").build();
        when(productService.filterByCategory("Tech")).thenReturn(Arrays.asList(p));

        mockMvc.perform(get("/api/products/category/Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Tech"));
    }
}
