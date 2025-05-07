package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.CategoryResponse;
import com.example.finmonitor.api.mapper.CategoryMapper;
import com.example.finmonitor.application.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@SecurityRequirement(name = "BearerAuth")
@Validated
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Получить все статусы
     */
    @GetMapping
    public List<CategoryResponse> getAllCategoryes() {
        return categoryService.getAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получить статус по UUID
     */
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable UUID id) {
        return categoryService.getById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id = " + id));
    }
}