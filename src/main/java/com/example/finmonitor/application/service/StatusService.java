package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatusService {
    List<Status> getAll();
    Optional<Status> getById(UUID id);
}