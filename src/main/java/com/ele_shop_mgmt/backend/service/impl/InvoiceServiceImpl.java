package com.ele_shop_mgmt.backend.service.impl;


import com.ele_shop_mgmt.backend.dto.request.InvoiceItemRequest;
import com.ele_shop_mgmt.backend.dto.request.InvoiceRequest;
import com.ele_shop_mgmt.backend.dto.response.InvoiceResponse;
import com.ele_shop_mgmt.backend.entity.Customer;
import com.ele_shop_mgmt.backend.entity.Invoice;
import com.ele_shop_mgmt.backend.entity.InvoiceItem;
import com.ele_shop_mgmt.backend.entity.Product;
import com.ele_shop_mgmt.backend.exception.BusinessException;
import com.ele_shop_mgmt.backend.exception.InsufficientStockException;
import com.ele_shop_mgmt.backend.exception.ResourceNotFoundException;
import com.ele_shop_mgmt.backend.repository.CustomerRepository;
import com.ele_shop_mgmt.backend.repository.InvoiceItemRepository;
import com.ele_shop_mgmt.backend.repository.InvoiceRepository;
import com.ele_shop_mgmt.backend.repository.ProductRepository;
import com.ele_shop_mgmt.backend.service.interfaces.InvoiceService;
import com.ele_shop_mgmt.backend.util.AppConstant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl  implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        // 1. Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // 2. Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Invoice must contain at least one item");
        }

        // 4. Prevent duplicate products
        Set<Long> productIds = new HashSet<>();
        for (InvoiceItemRequest itemRequest : request.getItems()) {
            if (!productIds.add(itemRequest.getProductId())) {
                throw new BusinessException("Duplicate products are not allowed in the same invoice");
            }
        }

        List<InvoiceItem> invoiceItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal gstAmount = BigDecimal.ZERO;

        // 3. Process each item
        for (InvoiceItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new BusinessException("Quantity must be greater than zero");
            }

            if (itemRequest.getQuantity() > product.getCurrentStock()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock
            product.setCurrentStock(product.getCurrentStock() - itemRequest.getQuantity());
            productRepository.save(product);

            BigDecimal unitSellingPrice = product.getSellingPrice();
            BigDecimal unitCostPrice = product.getPurchasePrice();
            BigDecimal gstPercentage = product.getGstPercentage();

            BigDecimal subtotal = unitSellingPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            // 2. Fix GST calculation with rounding
            BigDecimal itemGst = subtotal.multiply(gstPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            totalAmount = totalAmount.add(subtotal);
            gstAmount = gstAmount.add(itemGst);

            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .invoice(null) // will set after invoice is created
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitSellingPrice(unitSellingPrice)
                    .unitCostPrice(unitCostPrice)
                    .gstPercentage(gstPercentage)
                    .subtotal(subtotal)
                    .build();

            invoiceItems.add(invoiceItem);
        }

        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO;

        BigDecimal finalAmount = totalAmount.add(gstAmount).subtract(discountAmount);


        if (discountAmount.compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException("Discount amount cannot be negative");
        }

        if (paidAmount.compareTo(BigDecimal.ZERO) < 0){
            throw  new BusinessException("Paid amount cannot be negative.");
        }
        // 3. Add discount validation
        if (discountAmount.compareTo(totalAmount.add(gstAmount)) > 0 || finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Discount cannot exceed invoice amount");
        }

        BigDecimal pendingAmount = finalAmount.subtract(paidAmount);

        if (paidAmount.compareTo(finalAmount) > 0) {
            throw new BusinessException("Paid amount cannot exceed final amount");
        }

        // Determine payment status
        String paymentStatus;
        if (pendingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            paymentStatus = AppConstant.STATUS_PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = AppConstant.STATUS_PARTIAL;
        } else {
            paymentStatus = AppConstant.STATUS_PENDING;
        }

        // 1. Fix invoice number generation: INV-YYYY-{timestamp}
        String year = String.valueOf(LocalDateTime.now().getYear());
        String invoiceNumber = String.format("INV-%s-%d", year, System.currentTimeMillis());

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .invoiceDate(LocalDateTime.now())
                .customer(customer)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .gstAmount(gstAmount)
                .finalAmount(finalAmount)
                .paidAmount(paidAmount)
                .pendingAmount(pendingAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(paymentStatus)
                .build();

       Invoice savedinvoice = invoiceRepository.save(invoice);

        // 5. Optimize saving invoice items
        for (InvoiceItem item : invoiceItems){
            item.setInvoice(savedinvoice);
        }
        invoiceItemRepository.saveAll(invoiceItems);

        // Return response
        return InvoiceResponse.builder()
                .id(savedinvoice.getId())
                .invoiceNumber(savedinvoice.getInvoiceNumber())
                .invoiceDate(savedinvoice.getInvoiceDate())
                .customerName(customer.getName())
                .totalAmount(savedinvoice.getTotalAmount())
                .discountAmount(savedinvoice.getDiscountAmount())
                .gstAmount(savedinvoice.getGstAmount())
                .finalAmount(savedinvoice.getFinalAmount())
                .paidAmount(savedinvoice.getPaidAmount())
                .pendingAmount(savedinvoice.getPendingAmount())
                .paymentMethod(savedinvoice.getPaymentMethod())
                .paymentStatus(savedinvoice.getPaymentStatus())
                .build();
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoice -> InvoiceResponse.builder()
                        .id(invoice.getId())
                        .invoiceNumber(invoice.getInvoiceNumber())
                        .invoiceDate(invoice.getInvoiceDate())
                        .customerName(invoice.getCustomer().getName())
                        .totalAmount(invoice.getTotalAmount())
                        .discountAmount(invoice.getDiscountAmount())
                        .gstAmount(invoice.getGstAmount())
                        .finalAmount(invoice.getFinalAmount())
                        .paidAmount(invoice.getPaidAmount())
                        .pendingAmount(invoice.getPendingAmount())
                        .paymentMethod(invoice.getPaymentMethod())
                        .paymentStatus(invoice.getPaymentStatus())
                        .build())
                .toList();
    }

    @Override
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .customerName(invoice.getCustomer().getName())
                .totalAmount(invoice.getTotalAmount())
                .discountAmount(invoice.getDiscountAmount())
                .gstAmount(invoice.getGstAmount())
                .finalAmount(invoice.getFinalAmount())
                .paidAmount(invoice.getPaidAmount())
                .pendingAmount(invoice.getPendingAmount())
                .paymentMethod(invoice.getPaymentMethod())
                .paymentStatus(invoice.getPaymentStatus())
                .build();
    }
}
