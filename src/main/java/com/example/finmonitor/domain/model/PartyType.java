package com.example.finmonitor.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "party_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyType extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;
}