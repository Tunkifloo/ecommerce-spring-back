package com.springback.ecommerce_layers.service;

import com.springback.ecommerce_layers.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();
}