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
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all active products");
        List<Product> products = productRepository.findByActiveTrue();
        return products.stream()
                .map(ProductResponse::fromEntityBasic) // Sin imagen para mejor performance en listados
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
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
        log.info("Product created successfully with ID: {} and image: {}",
                savedProduct.getId(), savedProduct.hasImage() ? "Yes" : "No");

        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        return ProductResponse.fromEntity(product); // Con imagen completa para detalle
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Actualizar campos básicos si no son null
        if (request.name() != null) {
            // Validar nombre único para el vendedor (excluyendo el producto actual)
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
                log.info("Product image updated for ID: {}", id);
            } else {
                // Si se envía string vacío, eliminar imagen
                product.setImageData(null);
                product.setImageContentType(null);
                log.info("Product image removed for ID: {}", id);
            }
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());

        return ProductResponse.fromEntity(updatedProduct);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Soft delete - marcar como inactivo en lugar de eliminar
        product.setActive(false);
        productRepository.save(product);

        log.info("Product marked as inactive with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySeller(Long sellerId) {
        log.info("Fetching products for seller ID: {}", sellerId);

        // Verificar que el vendedor existe
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("Vendedor no encontrado con ID: " + sellerId);
        }

        List<Product> products = productRepository.findBySellerIdAndActiveTrue(sellerId);
        return products.stream()
                .map(ProductResponse::fromEntity) // Con imagen para productos específicos del vendedor
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        log.info("Fetching available products");
        List<Product> products = productRepository.findAvailableProducts();
        return products.stream()
                .map(ProductResponse::fromEntityBasic) // Sin imagen para listados
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(ProductResponse::fromEntityBasic) // Sin imagen para búsquedas
                .collect(Collectors.toList());
    }

    private void validateBase64Image(String base64Data) {
        try {
            // Verificar que sea Base64 válido
            java.util.Base64.getDecoder().decode(base64Data);

            // Verificar tamaño aproximado (Base64 aumenta ~33% el tamaño)
            // 2MB * 1.33 = ~2.66MB en Base64
            if (base64Data.length() > 2800000) {
                throw new ValidationException("La imagen es demasiado grande. Máximo 2MB");
            }

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Formato de imagen Base64 inválido");
        }
    }
}