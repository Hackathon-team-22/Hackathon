package com.example.finmonitor.domain.spec;

import com.example.finmonitor.domain.model.AuditLog;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.util.UUID;

public class AuditLogSpecification {

    /**
     * Фильтрация логов по пользователю.
     */
    public static Specification<AuditLog> forUser(UUID userId) {
        return (root, query, cb) ->
                cb.equal(root.join("changedByUser", JoinType.LEFT).get("id"), userId);
    }
}
