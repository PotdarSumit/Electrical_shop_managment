package com.ele_shop_mgmt.backend.service.interfaces;

import com.ele_shop_mgmt.backend.dto.request.ProductRequest;
import com.ele_shop_mgmt.backend.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    List<ProductResponse> searchProducts(String keyword);

    List<ProductResponse> getLowStockProducts();
}
