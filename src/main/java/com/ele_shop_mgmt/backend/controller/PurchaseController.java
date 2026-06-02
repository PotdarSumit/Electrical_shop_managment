package com.ele_shop_mgmt.backend.controller;

import com.ele_shop_mgmt.backend.dto.request.PurchaseRequest;
import com.ele_shop_mgmt.backend.dto.response.PurchaseResponse;
import com.ele_shop_mgmt.backend.service.interfaces.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    // Endpoint 1: Create Purchase
    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        PurchaseResponse response = purchaseService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint 2: Get All Purchases
    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAllPurchases() {
        List<PurchaseResponse> responses = purchaseService.getAllPurchases();
        return ResponseEntity.ok(responses);
    }

    // Endpoint 3: Get Purchase By Id
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable Long id) {
        PurchaseResponse response = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(response);
    }
}
