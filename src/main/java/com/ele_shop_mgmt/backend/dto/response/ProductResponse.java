package com.ele_shop_mgmt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String categoryName;
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
