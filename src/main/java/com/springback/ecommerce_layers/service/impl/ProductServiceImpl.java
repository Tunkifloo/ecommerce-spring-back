package com.springback.ecommerce_layers.service.impl;

import com.springback.ecommerce_layers.dto.response.ProductResponse;

import com.springback.ecommerce_layers.entity.Product;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springback.ecommerce_layers.repository.ProductRepository;
import com.springback.ecommerce_layers.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository = null;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}