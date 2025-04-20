package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StatusRepository extends JpaRepository<Status, UUID> {
}