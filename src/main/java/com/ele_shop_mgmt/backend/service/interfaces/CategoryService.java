package com.ele_shop_mgmt.backend.service.interfaces;

import com.ele_shop_mgmt.backend.dto.request.categoryRequest;
import com.ele_shop_mgmt.backend.dto.response.CategoryResponse;
import com.ele_shop_mgmt.backend.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(categoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id, categoryRequest request);

    void deleteCategory(Long id);
}
