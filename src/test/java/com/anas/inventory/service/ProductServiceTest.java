package com.anas.inventory.service;

import com.anas.inventory.dto.request.CreateProductRequest;
import com.anas.inventory.dto.response.ProductResponse;
import com.anas.inventory.entity.Product;
import com.anas.inventory.exception.DuplicateResourceException;
import com.anas.inventory.exception.ResourceNotFoundException;
import com.anas.inventory.repository.InventoryRepository;
import com.anas.inventory.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create product successfully when SKU is unique")
    void shouldCreateProductSuccessfully() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "SKU-TEST-001",
                "Mechanical Keyboard",
                "RGB Mechanical Gaming Keyboard",
                new BigDecimal("99.99"),
                50
        );

        when(productRepository.existsBySku(request.sku())).thenReturn(false);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setSku(request.sku());
        savedProduct.setName(request.name());
        savedProduct.setPrice(request.price());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse response = productService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sku()).isEqualTo("SKU-TEST-001");
        verify(productRepository, times(1)).save(any(Product.class));
        verify(inventoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when SKU already exists")
    void shouldThrowExceptionWhenSkuExists() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "SKU-DUPLICATE",
                "Product Name",
                null,
                new BigDecimal("10.00"),
                10
        );

        when(productRepository.existsBySku("SKU-DUPLICATE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Product with SKU already exists");

        // Verify save was never called
        verify(productRepository, never()).save(any(Product.class));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product ID does not exist")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }
}