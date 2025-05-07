package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.BankResponse;


import com.example.finmonitor.api.mapper.BankMapper;
import com.example.finmonitor.application.service.BankService;
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
@RequestMapping("/banks")
@SecurityRequirement(name = "BearerAuth")
@Validated
public class BankController {

    private final BankService bankService;
    private final BankMapper bankMapper;

    public BankController(BankService bankService, BankMapper bankMapper) {
        this.bankService = bankService;
        this.bankMapper = bankMapper;
    }

    /**
     * Получить все статусы
     */
    @GetMapping
    public List<BankResponse> getAllBankes() {
        return bankService.getAll().stream()
                .map(bankMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получить статус по UUID
     */
    @GetMapping("/{id}")
    public BankResponse getBankById(@PathVariable UUID id) {
        return bankService.getById(id)
                .map(bankMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bank not found with id = " + id));
    }
}