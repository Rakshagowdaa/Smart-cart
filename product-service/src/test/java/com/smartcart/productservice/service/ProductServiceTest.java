package com.smartcart.productservice.service;

import com.smartcart.productservice.entity.Product;
import com.smartcart.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct() {
        Product product = Product.builder().name("Phone").build();
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.addProduct(product);
        assertNotNull(result);
    }

    @Test
    void getProductById() {
        Product product = Product.builder().id(1L).name("Phone").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateStock() {
        Product product = Product.builder().id(1L).stockQuantity(10).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateStock(1L, 2);

        assertEquals(8, product.getStockQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct() {
        Product existingProduct = Product.builder().id(1L).name("Old Name").build();
        Product newProduct = Product.builder().name("New Name").build();
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product result = productService.updateProduct(1L, newProduct);
        assertEquals("New Name", result.getName());
    }

    @Test
    void deleteProduct() {
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(java.util.Arrays.asList(new Product()));
        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    void searchProducts() {
        when(productRepository.findByNameContainingIgnoreCase("Phone")).thenReturn(java.util.Arrays.asList(new Product()));
        assertEquals(1, productService.searchProducts("Phone").size());
    }

    @Test
    void filterByCategory() {
        when(productRepository.findByCategoryIgnoreCase("Electronics")).thenReturn(java.util.Arrays.asList(new Product()));
        assertEquals(1, productService.filterByCategory("Electronics").size());
    }
}
