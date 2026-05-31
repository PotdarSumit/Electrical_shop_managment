package com.ele_shop_mgmt.backend.service.impl;

import com.ele_shop_mgmt.backend.dto.request.ProductRequest;
import com.ele_shop_mgmt.backend.dto.response.ProductResponse;
import com.ele_shop_mgmt.backend.entity.Category;
import com.ele_shop_mgmt.backend.entity.Product;
import com.ele_shop_mgmt.backend.exception.ResourceNotFoundException;
import com.ele_shop_mgmt.backend.repository.CategoryRepository;
import com.ele_shop_mgmt.backend.repository.ProductRepository;
import com.ele_shop_mgmt.backend.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;


    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .category(category)
                .brand(request.getBrand())
                .model(request.getModel())
                .hp(request.getHp())
                .stage(request.getStage())
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .currentStock(request.getCurrentStock())
                .minimumStockAlert(request.getMinimumStockAlert())
                .warrantyPeriod(request.getWarrantyPeriod())
                .gstPercentage(request.getGstPercentage())
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());
        product.setHp(request.getHp());
        product.setStage(request.getStage());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setCurrentStock(request.getCurrentStock());
        product.setMinimumStockAlert(request.getMinimumStockAlert());
        product.setWarrantyPeriod(request.getWarrantyPeriod());
        product.setGstPercentage(request.getGstPercentage());

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
               .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryName(product.getCategory().getName())
                .brand(product.getBrand())
                .model(product.getModel())
                .hp(product.getHp())
                .stage(product.getStage())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .currentStock(product.getCurrentStock())
                .minimumStockAlert(product.getMinimumStockAlert())
                .warrantyPeriod(product.getWarrantyPeriod())
                .gstPercentage(product.getGstPercentage())
                .build();
    }
}
