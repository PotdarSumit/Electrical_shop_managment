package com.ele_shop_mgmt.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String brand;

    @Size(max = 100, message = "Model must not exceed 100 characters")
    @Column(length = 100)
    private String model;

    @PositiveOrZero(message = "HP must be zero or positive")
    private Integer hp;

    @Size(max = 50, message = "Stage must not exceed 50 characters")
    @Column(length = 50)
    private String stage;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Purchase price must be a valid monetary amount")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Selling price must be a valid monetary amount")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @PositiveOrZero(message = "Current stock must be zero or positive")
    @Column(nullable = false)
    private Integer currentStock;

    @PositiveOrZero(message = "Minimum stock alert must be zero or positive")
    private Integer minimumStockAlert;

    @PositiveOrZero(message = "Warranty period must be zero or positive")
    private Integer warrantyPeriod;

    @DecimalMin(value = "0.0", inclusive = true, message = "GST percentage must be non-negative")
    @Digits(integer = 3, fraction = 2, message = "GST percentage must be a valid percentage")
    @Column(precision = 5, scale = 2)
    private BigDecimal gstPercentage;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
