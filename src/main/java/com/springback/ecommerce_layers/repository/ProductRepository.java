package com.springback.ecommerce_layers.repository;

import com.springback.ecommerce_layers.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.active = true")
    List<Product> findByActiveTrueWithSeller();

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.active = true AND p.stock > 0")
    List<Product> findAvailableProductsWithSeller();

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.seller.id = :sellerId AND p.active = true")
    List<Product> findBySellerIdAndActiveTrueWithSeller(@Param("sellerId") Long sellerId);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.id = :id")
    Optional<Product> findByIdWithSeller(@Param("id") Long id);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCaseWithSeller(@Param("name") String name);

    boolean existsByNameAndSellerIdAndActiveTrue(String name, Long sellerId);
}