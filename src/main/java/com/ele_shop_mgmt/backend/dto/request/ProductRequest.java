package com.ele_shop_mgmt.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private Long categoryId;
    private String brand;
    private String model;
    private Integer hp;
    private String stage;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer currentStock;
    private Integer minimumStockAlert;
    private Integer warrantyPeriod;
    private BigDecimal gstPercentage;
}
