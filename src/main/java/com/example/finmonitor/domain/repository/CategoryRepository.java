package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}