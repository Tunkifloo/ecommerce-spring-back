package com.springback.ecommerce_layers;

import com.springback.ecommerce_layers.entity.User;
import com.springback.ecommerce_layers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
public class ECommerceLayersApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(ECommerceLayersApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User defaultAdmin = User.builder()
                        .firstName("Admin")
                        .lastName("System")
                        .email("admin@ecommerce.com")
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .phone("000000000")
                        .role(User.UserRole.ADMIN)
                        .active(true)
                        .enabled(true)
                        .firstLogin(false)
                        .build();

                userRepository.save(defaultAdmin);
                System.out.println("Usuario admin creado: admin/admin123");
            }
        };
    }
}