package com.ele_shop_mgmt.backend.service.interfaces;

import com.ele_shop_mgmt.backend.dto.request.InvoiceRequest;
import com.ele_shop_mgmt.backend.dto.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);
    List<InvoiceResponse> getAllInvoices();
    InvoiceResponse getInvoiceById(Long id);
}
