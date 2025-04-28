package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartyTypeRepository extends JpaRepository<PartyType, UUID> {

    Optional<PartyType> findByName(String name);

    default UUID getOrCreateByName(String name) {
        return findByName(name)
                .map(PartyType::getId)
                .orElseGet(() -> save(new PartyType(name)).getId());
    }
}