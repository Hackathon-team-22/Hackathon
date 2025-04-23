package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {
    Optional<Status> findByName(String name);

    default UUID getOrCreateByName(String name) {
        return findByName(name)
                .map(Status::getId)
                .orElseGet(() -> save(new Status(name)).getId());
    }
}
