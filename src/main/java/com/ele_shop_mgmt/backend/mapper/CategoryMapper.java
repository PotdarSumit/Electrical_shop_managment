package com.ele_shop_mgmt.backend.mapper;

import com.ele_shop_mgmt.backend.dto.request.categoryRequest;
import com.ele_shop_mgmt.backend.dto.response.CategoryResponse;
import com.ele_shop_mgmt.backend.entity.Category;

public final class CategoryMapper {
    private CategoryMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    public static Category toEntity(categoryRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

}
