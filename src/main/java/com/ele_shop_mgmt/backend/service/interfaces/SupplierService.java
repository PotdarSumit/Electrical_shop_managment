package com.ele_shop_mgmt.backend.service.interfaces;

import com.ele_shop_mgmt.backend.dto.request.SupplierRequest;
import com.ele_shop_mgmt.backend.dto.response.SupplierResponse;

import java.util.List;
public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    SupplierResponse getSupplierById(Long id);
    List<SupplierResponse> getAllSuppliers();
    void deleteSupplier(Long id);
}
