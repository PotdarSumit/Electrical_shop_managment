package com.ele_shop_mgmt.backend.repository;

import com.ele_shop_mgmt.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.minimumStockAlert")
    List<Product> findLowStockProducts();


}
