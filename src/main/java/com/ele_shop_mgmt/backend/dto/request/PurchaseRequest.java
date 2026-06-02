package com.ele_shop_mgmt.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {
    private Long supplierId;
    private BigDecimal paidAmount;
    private List<PurchaseItemRequest> items;
}
