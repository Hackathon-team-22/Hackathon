package com.example.finmonitor.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;
}