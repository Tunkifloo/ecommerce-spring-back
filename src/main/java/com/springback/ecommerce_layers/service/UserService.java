package com.springback.ecommerce_layers.service;

import com.springback.ecommerce_layers.dto.request.LoginRequest;
import com.springback.ecommerce_layers.dto.request.UserCreateRequest;
import com.springback.ecommerce_layers.dto.request.UserUpdateRequest;
import com.springback.ecommerce_layers.dto.response.TokenResponse;
import com.springback.ecommerce_layers.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    List<UserResponse> getActiveUsers();

    List<UserResponse> getUsersByRole(String role);

    TokenResponse login(LoginRequest request);

    UserResponse getCurrentUser(String username);
}