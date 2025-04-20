package com.example.finmonitor.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;
}
