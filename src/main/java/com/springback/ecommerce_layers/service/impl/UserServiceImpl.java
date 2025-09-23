package com.springback.ecommerce_layers.service.impl;

import com.springback.ecommerce_layers.config.security.JwtService;
import com.springback.ecommerce_layers.dto.request.LoginRequest;
import com.springback.ecommerce_layers.dto.request.UserCreateRequest;
import com.springback.ecommerce_layers.dto.request.UserUpdateRequest;
import com.springback.ecommerce_layers.dto.response.TokenResponse;
import com.springback.ecommerce_layers.dto.response.UserResponse;
import com.springback.ecommerce_layers.entity.User;
import com.springback.ecommerce_layers.exception.ResourceNotFoundException;
import com.springback.ecommerce_layers.exception.ValidationException;
import com.springback.ecommerce_layers.repository.UserRepository;
import com.springback.ecommerce_layers.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating user with email: {} and username: {}", request.email(), request.username());

        // Validar que el email no exista
        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Ya existe un usuario con el email: " + request.email());
        }

        // Validar que el username no exista
        if (userRepository.existsByUsername(request.username())) {
            throw new ValidationException("Ya existe un usuario con el username: " + request.username());
        }

        // Crear y guardar usuario
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .username(request.username()) // Usar el username proporcionado, no el email
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(request.role())
                .active(true)
                .enabled(true)
                .firstLogin(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {} and username: {}", savedUser.getId(), savedUser.getUsername());

        return UserResponse.fromEntity(savedUser);
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Validar email único si se está actualizando
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new ValidationException("Ya existe un usuario con el email: " + request.email());
            }
            user.setEmail(request.email());
            // NO cambiar el username automáticamente cuando se cambia el email
        }

        // Actualizar campos si no son null
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.info("Fetching active users");
        return userRepository.findByActiveTrue()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String role) {
        log.info("Fetching users by role: {}", role);
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            return userRepository.findByRole(userRole)
                    .stream()
                    .map(UserResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Rol no válido: " + role);
        }
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.username());

        try {
            // Verificar si el usuario existe antes de autenticar
            User user = userRepository.findByUsername(request.username())
                    .or(() -> userRepository.findByEmail(request.username()))
                    .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

            if (!user.getActive()) {
                throw new BadCredentialsException("Usuario inactivo");
            }

            // Verificar la contraseña manualmente antes de usar AuthenticationManager
            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                throw new BadCredentialsException("Credenciales inválidas");
            }

            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generar token
            String token = jwtService.getToken(userDetails, user);

            log.info("Login successful for user: {}", request.username());

            return new TokenResponse(
                    token,
                    user.getFirstLogin(),
                    user.getUsername(),
                    user.getRole().name()
            );

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.username(), e);
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        log.info("Fetching current user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return UserResponse.fromEntity(user);
    }
}
