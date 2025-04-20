package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {
    /**
     * Находит статус по его наименованию.
     * @param name название статуса (например, "Deleted")
     * @return Optional со статусом, или пустой, если не найден
     */
    Optional<Status> findByName(String name);
}
