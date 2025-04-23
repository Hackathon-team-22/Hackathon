package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Category;
import com.example.finmonitor.domain.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);


    default UUID getOrCreateByName(String name) {
        return findByName(name)
                .map(Category::getId)
                .orElseGet(() -> save(new Category(name)).getId());
    }

}