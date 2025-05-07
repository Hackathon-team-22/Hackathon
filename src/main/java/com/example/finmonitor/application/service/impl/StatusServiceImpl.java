package com.example.finmonitor.application.service.impl;

import com.example.finmonitor.application.service.StatusService;
import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.repository.StatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;

    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public List<Status> getAll() {
        return statusRepository.findAll();
    }

    @Override
    public Optional<Status> getById(UUID id) {
        return statusRepository.findById(id);
    }
}