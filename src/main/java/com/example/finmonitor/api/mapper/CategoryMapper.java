package com.example.finmonitor.api.mapper;

import com.example.finmonitor.api.dto.CategoryResponse;
import com.example.finmonitor.domain.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
