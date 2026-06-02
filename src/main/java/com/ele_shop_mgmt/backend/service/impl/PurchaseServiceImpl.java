package com.ele_shop_mgmt.backend.service.impl;


import com.ele_shop_mgmt.backend.dto.request.PurchaseItemRequest;
import com.ele_shop_mgmt.backend.dto.request.PurchaseRequest;
import com.ele_shop_mgmt.backend.dto.response.PurchaseResponse;
import com.ele_shop_mgmt.backend.entity.Product;
import com.ele_shop_mgmt.backend.entity.Purchase;
import com.ele_shop_mgmt.backend.entity.PurchaseItem;
import com.ele_shop_mgmt.backend.entity.Supplier;
import com.ele_shop_mgmt.backend.exception.BusinessException;
import com.ele_shop_mgmt.backend.exception.ResourceNotFoundException;
import com.ele_shop_mgmt.backend.repository.ProductRepository;
import com.ele_shop_mgmt.backend.repository.PurchaseItemRepository;
import com.ele_shop_mgmt.backend.repository.PurchaseRepository;
import com.ele_shop_mgmt.backend.repository.SupplierRepository;
import com.ele_shop_mgmt.backend.service.interfaces.PurchaseService;
import com.ele_shop_mgmt.backend.util.AppConstant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        // 1. Validate supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        // 2. Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Purchase must contain at least one item");
        }

        // 3. Prevent duplicate products
        Set<Long> productIds = new HashSet<>();
        for (PurchaseItemRequest itemRequest : request.getItems()) {
            if (!productIds.add(itemRequest.getProductId())) {
                throw new BusinessException("Duplicate products are not allowed in the same purchase");
            }
        }

        List<PurchaseItem> purchaseItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. Process each item
        for (PurchaseItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new BusinessException("Quantity must be greater than zero");
            }

            if (itemRequest.getPurchasePrice() == null || itemRequest.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Purchase price must be greater than zero");
            }

            // 5. Increase stock
            product.setCurrentStock(product.getCurrentStock() + itemRequest.getQuantity());

            // 6. Update latest purchase price
            product.setPurchasePrice(itemRequest.getPurchasePrice());
            productRepository.save(product);

            // 7. Calculate subtotal
            BigDecimal subtotal = itemRequest.getPurchasePrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            // 8. Create PurchaseItem
            PurchaseItem purchaseItem = PurchaseItem.builder()
                    .purchase(null) // will set later
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .purchasePrice(itemRequest.getPurchasePrice())
                    .gstPercentage(product.getGstPercentage())
                    .subtotal(subtotal)
                    .build();

            purchaseItems.add(purchaseItem);
            totalAmount = totalAmount.add(subtotal);
        }

        // 10. Validate paidAmount
        BigDecimal paidAmount = request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO;
        if (paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Paid amount cannot be negative");
        }
        if (paidAmount.compareTo(totalAmount) > 0) {
            throw new BusinessException("Paid amount cannot exceed total amount");
        }

        // 11. Calculate pendingAmount
        BigDecimal pendingAmount = totalAmount.subtract(paidAmount);

        // 12. Determine payment status
        String paymentStatus;
        if (pendingAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = AppConstant.STATUS_PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = AppConstant.STATUS_PARTIAL;
        } else {
            paymentStatus = AppConstant.STATUS_PENDING;
        }

        // 13. Generate purchase number
        String year = String.valueOf(LocalDateTime.now().getYear());
        String purchaseNumber = String.format("PUR-%s-%d", year, System.currentTimeMillis());

        // 14. Create Purchase entity
        Purchase purchase = Purchase.builder()
                .purchaseNumber(purchaseNumber)
                .purchaseDate(LocalDateTime.now())
                .supplier(supplier)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .pendingAmount(pendingAmount)
                .paymentStatus(paymentStatus)
                .build();

        Purchase savedpurchase = purchaseRepository.save(purchase);

        // 15. Set purchase reference and save items
        for (PurchaseItem item : purchaseItems){
            item.setPurchase(savedpurchase);
        }
        purchaseItemRepository.saveAll(purchaseItems);

        // 16. Update supplier pending payment
        BigDecimal currentPending = supplier.getPendingPayment() != null ? supplier.getPendingPayment() : BigDecimal.ZERO;
        supplier.setPendingPayment(currentPending.add(pendingAmount));
        supplierRepository.save(supplier);

        // 17. Return response
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .purchaseNumber(purchase.getPurchaseNumber())
                .purchaseDate(purchase.getPurchaseDate())
                .supplierName(supplier.getName())
                .totalAmount(purchase.getTotalAmount())
                .paidAmount(purchase.getPaidAmount())
                .pendingAmount(purchase.getPendingAmount())
                .paymentStatus(purchase.getPaymentStatus())
                .build();
    }

    @Override
    public List<PurchaseResponse> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(purchase -> PurchaseResponse.builder()
                        .id(purchase.getId())
                        .purchaseNumber(purchase.getPurchaseNumber())
                        .purchaseDate(purchase.getPurchaseDate())
                        .supplierName(purchase.getSupplier().getName())
                        .totalAmount(purchase.getTotalAmount())
                        .paidAmount(purchase.getPaidAmount())
                        .pendingAmount(purchase.getPendingAmount())
                        .paymentStatus(purchase.getPaymentStatus())
                        .build())
                .toList();
    }

    @Override
    public PurchaseResponse getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));

        return PurchaseResponse.builder()
                .id(purchase.getId())
                .purchaseNumber(purchase.getPurchaseNumber())
                .purchaseDate(purchase.getPurchaseDate())
                .supplierName(purchase.getSupplier().getName())
                .totalAmount(purchase.getTotalAmount())
                .paidAmount(purchase.getPaidAmount())
                .pendingAmount(purchase.getPendingAmount())
                .paymentStatus(purchase.getPaymentStatus())
                .build();
    }
}
