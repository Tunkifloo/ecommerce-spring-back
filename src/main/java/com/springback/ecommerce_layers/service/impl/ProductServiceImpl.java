package com.springback.ecommerce_layers.service.impl;

import com.springback.ecommerce_layers.dto.request.ProductCreateRequest;
import com.springback.ecommerce_layers.dto.request.ProductUpdateRequest;
import com.springback.ecommerce_layers.dto.response.ProductResponse;
import com.springback.ecommerce_layers.entity.Product;
import com.springback.ecommerce_layers.entity.User;
import com.springback.ecommerce_layers.exception.ResourceNotFoundException;
import com.springback.ecommerce_layers.exception.ValidationException;
import com.springback.ecommerce_layers.repository.ProductRepository;
import com.springback.ecommerce_layers.repository.UserRepository;
import com.springback.ecommerce_layers.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all active products");
        // USAR FETCH JOIN para evitar N+1
        List<Product> products = productRepository.findByActiveTrueWithSeller();
        return products.stream()
                .map(ProductResponse::fromEntityBasic)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating product with name: {} for seller: {}", request.name(), request.sellerId());

        // Validar que el vendedor existe y puede vender
        User seller = userRepository.findById(request.sellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con ID: " + request.sellerId()));

        if (!seller.canSellProducts()) {
            throw new ValidationException("El usuario no tiene permisos para vender productos");
        }

        // Validar que no existe un producto activo con el mismo nombre para este vendedor
        if (productRepository.existsByNameAndSellerIdAndActiveTrue(request.name(), request.sellerId())) {
            throw new ValidationException("Ya existe un producto activo con ese nombre para este vendedor");
        }

        // Validar imagen Base64 si existe
        if (request.imageData() != null && !request.imageData().trim().isEmpty()) {
            validateBase64Image(request.imageData());
        }

        // Crear producto
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .seller(seller)
                .active(true)
                .imageData(request.imageData())
                .imageContentType(request.imageContentType())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        // USAR FETCH JOIN para cargar seller en una sola consulta
        Product product = productRepository.findByIdWithSeller(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        return ProductResponse.fromEntity(product);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", id);

        // USAR FETCH JOIN para evitar consultas adicionales
        Product product = productRepository.findByIdWithSeller(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Actualizar campos básicos si no son null
        if (request.name() != null) {
            if (!request.name().equals(product.getName()) &&
                    productRepository.existsByNameAndSellerIdAndActiveTrue(request.name(), product.getSeller().getId())) {
                throw new ValidationException("Ya existe un producto activo con ese nombre para este vendedor");
            }
            product.setName(request.name());
        }

        if (request.description() != null) {
            product.setDescription(request.description());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (request.stock() != null) {
            product.setStock(request.stock());
        }

        if (request.active() != null) {
            product.setActive(request.active());
        }

        // Actualizar imagen si se proporciona
        if (request.imageData() != null) {
            if (!request.imageData().trim().isEmpty()) {
                validateBase64Image(request.imageData());
                product.setImageData(request.imageData());
                product.setImageContentType(request.imageContentType());
            } else {
                product.setImageData(null);
                product.setImageContentType(null);
            }
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());

        return ProductResponse.fromEntity(updatedProduct);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        try {
            // Buscar el producto sin fetch join para delete
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

            log.info("Found product to delete: {} (Active: {})", product.getName(), product.getActive());

            // Soft delete - marcar como inactivo
            product.setActive(false);

            // Guardar cambios
            Product deletedProduct = productRepository.save(product);

            log.info("Product marked as inactive successfully with ID: {} (Active: {})",
                    deletedProduct.getId(), deletedProduct.getActive());

        } catch (Exception e) {
            log.error("Error deleting product with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySeller(Long sellerId) {
        log.info("Fetching products for seller ID: {} - SINGLE CALL", sellerId);

        // Verificar que el vendedor existe
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("Vendedor no encontrado con ID: " + sellerId);
        }

        // USAR FETCH JOIN para evitar N+1 queries
        List<Product> products = productRepository.findBySellerIdAndActiveTrueWithSeller(sellerId);
        log.info("Found {} products for seller {}", products.size(), sellerId);

        return products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        log.info("Fetching available products");
        // USAR FETCH JOIN
        List<Product> products = productRepository.findAvailableProductsWithSeller();
        return products.stream()
                .map(ProductResponse::fromEntityBasic)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        // USAR FETCH JOIN
        List<Product> products = productRepository.findByNameContainingIgnoreCaseWithSeller(name);
        return products.stream()
                .map(ProductResponse::fromEntityBasic)
                .collect(Collectors.toList());
    }

    private void validateBase64Image(String base64Data) {
        try {
            java.util.Base64.getDecoder().decode(base64Data);
            if (base64Data.length() > 2800000) {
                throw new ValidationException("La imagen es demasiado grande. Máximo 2MB");
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Formato de imagen Base64 inválido");
        }
    }
}