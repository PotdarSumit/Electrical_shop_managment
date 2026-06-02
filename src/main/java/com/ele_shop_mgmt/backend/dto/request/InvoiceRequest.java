package com.ele_shop_mgmt.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class InvoiceRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private BigDecimal discountAmount;

    private BigDecimal paidAmount;

    @Valid
    @NotNull(message = "Invoice items are required")
    private List<InvoiceItemRequest> items;
}
