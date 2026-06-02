package com.ele_shop_mgmt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private String customerName;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal gstAmount;
    private BigDecimal finalAmount;

    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;

    private String paymentMethod;
    private String paymentStatus;
}
