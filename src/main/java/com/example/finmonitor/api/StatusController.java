package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.StatusResponse;
import com.example.finmonitor.api.mapper.StatusMapper;
import com.example.finmonitor.application.service.StatusService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/status")
@SecurityRequirement(name = "BearerAuth")
@Validated
public class StatusController {

    private final StatusService statusService;
    private final StatusMapper statusMapper;

    public StatusController(StatusService statusService, StatusMapper statusMapper) {
        this.statusService = statusService;
        this.statusMapper = statusMapper;
    }

    /**
     * Получить все статусы
     */
    @GetMapping
    public List<StatusResponse> getAllStatuses() {
        return statusService.getAll().stream()
                .map(statusMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получить статус по UUID
     */
    @GetMapping("/{id}")
    public StatusResponse getStatusById(@PathVariable UUID id) {
        return statusService.getById(id)
                .map(statusMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Status not found with id = " + id));
    }
}