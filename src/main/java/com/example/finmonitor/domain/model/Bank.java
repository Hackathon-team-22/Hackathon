package com.example.finmonitor.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bank extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;
}
