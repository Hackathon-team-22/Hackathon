package com.example.finmonitor.application.service.impl;

import com.example.finmonitor.application.service.CategoryService;
import com.example.finmonitor.domain.model.Category;
import com.example.finmonitor.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getById(UUID id) {
        return categoryRepository.findById(id);
    }
}