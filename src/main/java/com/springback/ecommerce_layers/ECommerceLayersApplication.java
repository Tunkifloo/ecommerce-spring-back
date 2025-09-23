package com.springback.ecommerce_layers;

import com.springback.ecommerce_layers.entity.Product;
import com.springback.ecommerce_layers.entity.User;
import com.springback.ecommerce_layers.repository.ProductRepository;
import com.springback.ecommerce_layers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
public class ECommerceLayersApplication {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(ECommerceLayersApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                LocalDateTime now = LocalDateTime.now();

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
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

                userRepository.save(defaultAdmin);
                System.out.println("Usuario admin creado: admin/admin123");
            }

            createSampleUsers();

            createSampleProducts();
        };
    }

    private void createSampleUsers() {
        if (userRepository.findByUsername("ana.rodriguez").isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            User customer = User.builder()
                    .firstName("Ana")
                    .lastName("Rodríguez")
                    .email("ana.rodriguez@email.com")
                    .username("ana.rodriguez")
                    .password(passwordEncoder.encode("password123"))
                    .phone("+51987654321")
                    .role(User.UserRole.CUSTOMER)
                    .active(true)
                    .enabled(true)
                    .firstLogin(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            userRepository.save(customer);
            System.out.println("Usuario customer creado: ana.rodriguez/password123");
        }

        if (userRepository.findByUsername("carlos.seller").isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            User seller = User.builder()
                    .firstName("Carlos")
                    .lastName("Mendoza")
                    .email("carlos.seller@email.com")
                    .username("carlos.seller")
                    .password(passwordEncoder.encode("password123"))
                    .phone("+51987654322")
                    .role(User.UserRole.SELLER)
                    .active(true)
                    .enabled(true)
                    .firstLogin(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            userRepository.save(seller);
            System.out.println("Usuario seller creado: carlos.seller/password123");
        }
    }

    private void createSampleProducts() {
        if (productRepository.count() == 0) {
            User seller = userRepository.findByUsername("carlos.seller")
                    .orElse(userRepository.findByUsername("admin").orElse(null));

            if (seller != null) {
                LocalDateTime now = LocalDateTime.now();

                Product[] products = {
                        Product.builder()
                                .name("MacBook Pro M3 14\"")
                                .description("Laptop Apple MacBook Pro de 14 pulgadas con chip M3, 16GB RAM, 512GB SSD. Perfecta para profesionales creativos y desarrolladores.")
                                .price(new BigDecimal("2499.99"))
                                .stock(5)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("Dell XPS 13 Plus")
                                .description("Ultrabook Dell XPS 13 Plus con Intel Core i7, 16GB RAM, 1TB SSD, pantalla OLED 4K. Diseño premium y rendimiento excepcional.")
                                .price(new BigDecimal("1899.99"))
                                .stock(8)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("ASUS ROG Strix G15")
                                .description("Laptop gaming ASUS ROG Strix G15 con AMD Ryzen 7, RTX 4060, 16GB RAM, 512GB SSD. Diseñada para gaming de alto rendimiento.")
                                .price(new BigDecimal("1299.99"))
                                .stock(12)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("iPhone 15 Pro Max")
                                .description("iPhone 15 Pro Max de 256GB con titanio natural, cámara de 48MP, chip A17 Pro. La innovación de Apple en tu bolsillo.")
                                .price(new BigDecimal("1399.99"))
                                .stock(15)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("Samsung Galaxy S24 Ultra")
                                .description("Samsung Galaxy S24 Ultra de 512GB con S Pen incluido, cámara de 200MP, pantalla Dynamic AMOLED 6.8\". Productividad y entretenimiento.")
                                .price(new BigDecimal("1299.99"))
                                .stock(10)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("Google Pixel 8 Pro")
                                .description("Google Pixel 8 Pro de 256GB con chip Tensor G3, cámara computacional avanzada, 7 años de actualizaciones garantizadas.")
                                .price(new BigDecimal("999.99"))
                                .stock(20)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("AirPods Pro 2da Gen")
                                .description("Apple AirPods Pro de segunda generación con cancelación activa de ruido, audio espacial y estuche MagSafe.")
                                .price(new BigDecimal("249.99"))
                                .stock(25)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("Sony WH-1000XM5")
                                .description("Audífonos inalámbricos Sony WH-1000XM5 con la mejor cancelación de ruido del mercado, batería de 30 horas.")
                                .price(new BigDecimal("399.99"))
                                .stock(18)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("PlayStation 5 Slim")
                                .description("Consola PlayStation 5 Slim con 1TB de almacenamiento SSD, tecnología 3D audio y gráficos 4K a 120fps.")
                                .price(new BigDecimal("499.99"))
                                .stock(7)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build(),

                        Product.builder()
                                .name("Steam Deck OLED")
                                .description("Consola portátil Steam Deck OLED de 512GB, pantalla HDR OLED, acceso a toda la biblioteca de Steam en cualquier lugar.")
                                .price(new BigDecimal("649.99"))
                                .stock(6)
                                .active(true)
                                .seller(seller)
                                .createdAt(now)
                                .updatedAt(now)
                                .build()
                };

                productRepository.saveAll(java.util.Arrays.asList(products));
                System.out.println("OK " + products.length + " productos tecnológicos creados para " + seller.getUsername());
            }
        }
    }
}