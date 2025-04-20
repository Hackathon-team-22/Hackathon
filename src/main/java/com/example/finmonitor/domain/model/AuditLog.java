package com.example.finmonitor.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    private UUID id;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @ManyToOne
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private User changedBy;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(columnDefinition = "JSONB", nullable = false)
    private String changes;
}