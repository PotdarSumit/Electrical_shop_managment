package com.ele_shop_mgmt.backend.service.interfaces;

import com.ele_shop_mgmt.backend.dto.request.PurchaseRequest;
import com.ele_shop_mgmt.backend.dto.response.PurchaseResponse;

import java.util.List;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request);

    List<PurchaseResponse> getAllPurchases();

    PurchaseResponse getPurchaseById(Long id);
}
