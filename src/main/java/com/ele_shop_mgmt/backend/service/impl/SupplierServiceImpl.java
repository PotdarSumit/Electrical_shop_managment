package com.ele_shop_mgmt.backend.service.impl;

import com.ele_shop_mgmt.backend.dto.request.SupplierRequest;
import com.ele_shop_mgmt.backend.dto.response.SupplierResponse;
import com.ele_shop_mgmt.backend.entity.Supplier;
import com.ele_shop_mgmt.backend.exception.ResourceNotFoundException;
import com.ele_shop_mgmt.backend.repository.SupplierRepository;
import com.ele_shop_mgmt.backend.service.interfaces.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .gstNumber(request.getGstNumber())
                .pendingPayment(request.getPendingPayment())
                .build();
        supplier = supplierRepository.save(supplier);
        return toResponse(supplier);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplier.setName(request.getName());
        supplier.setContactNumber(request.getContactNumber());
        supplier.setAddress(request.getAddress());
        supplier.setGstNumber(request.getGstNumber());
        supplier.setPendingPayment(request.getPendingPayment());

        supplier = supplierRepository.save(supplier);
        return toResponse(supplier);
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        return toResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        supplierRepository.delete(supplier);
    }

    private SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactNumber(supplier.getContactNumber())
                .address(supplier.getAddress())
                .gstNumber(supplier.getGstNumber())
                .pendingPayment(supplier.getPendingPayment())
                .build();
    }
}
