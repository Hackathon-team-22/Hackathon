package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {
    List<Category> getAll();
    Optional<Category> getById(UUID id);
}