package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.TransactionTypeResponse;
import com.example.finmonitor.api.mapper.TransactionTypeMapper;
import com.example.finmonitor.application.service.TransactionTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transaction-types")
@SecurityRequirement(name = "BearerAuth")
@Validated
public class TransactionTypeController {

    private final TransactionTypeService transactioTypeService;
    private final TransactionTypeMapper transactioTypeMapper;

    public TransactionTypeController(TransactionTypeService transactioTypeService, TransactionTypeMapper transactioTypeMapper) {
        this.transactioTypeService = transactioTypeService;
        this.transactioTypeMapper = transactioTypeMapper;
    }

    /**
     * Получить все статусы
     */
    @GetMapping
    public List<TransactionTypeResponse> getAllTransactionTypees() {
        return transactioTypeService.getAll().stream()
                .map(transactioTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получить статус по UUID
     */
    @GetMapping("/{id}")
    public TransactionTypeResponse getTransactionTypeById(@PathVariable UUID id) {
        return transactioTypeService.getById(id)
                .map(transactioTypeMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "TransactionType not found with id = " + id));
    }
}