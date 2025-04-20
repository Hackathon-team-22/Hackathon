package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PartyTypeRepository extends JpaRepository<PartyType, UUID> {
}