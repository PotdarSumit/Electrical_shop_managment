package com.ele_shop_mgmt.backend.mapper;

import com.ele_shop_mgmt.backend.dto.response.ProductResponse;
import com.ele_shop_mgmt.backend.entity.Product;

public final class ProductMapper {
    private ProductMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brand(product.getBrand())
                .model(product.getModel())
                .hp(product.getHp())
                .stage(product.getStage())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .currentStock(product.getCurrentStock())
                .minimumStockAlert(product.getMinimumStockAlert())
                .warrantyPeriod(product.getWarrantyPeriod())
                .gstPercentage(product.getGstPercentage())
                .build();
    }
}
