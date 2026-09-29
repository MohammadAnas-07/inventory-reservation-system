package com.anas.inventory.service;

import com.anas.inventory.dto.request.CreateProductRequest;
import com.anas.inventory.dto.response.ProductResponse;
import com.anas.inventory.entity.Inventory;
import com.anas.inventory.entity.Product;
import com.anas.inventory.exception.DuplicateResourceException;
import com.anas.inventory.exception.ResourceNotFoundException;
import com.anas.inventory.repository.InventoryRepository;
import com.anas.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    // Spring Boot in dono repositories ko automatically inject karega
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // 1. Check duplicate SKU
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU already exists: " + request.sku());
        }

        // 2. Product Entity create karo
        Product product = new Product();
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());

        Product savedProduct = productRepository.save(product);

        // 3. Sath hi is product ka Inventory record bhi initialize karo
        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setTotalQuantity(request.initialStock() != null ? request.initialStock() : 0);
        inventory.setReservedQuantity(0);

        inventoryRepository.save(inventory);

        // 4. Response DTO return karo
        return ProductResponse.fromEntity(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
}