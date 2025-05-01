package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.TransactionRequest;
import com.example.finmonitor.api.dto.TransactionResponse;
import com.example.finmonitor.application.service.CreateTransactionService;
import com.example.finmonitor.application.service.DeleteTransactionService;
import com.example.finmonitor.application.service.FilterTransactionsService;
import com.example.finmonitor.application.service.GetTransactionService;
import com.example.finmonitor.application.service.UpdateTransactionService;
import com.example.finmonitor.domain.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@SecurityRequirement(name = "BearerAuth")
public class TransactionController {

    @Autowired private CreateTransactionService createService;
    @Autowired private GetTransactionService getService;
    @Autowired private UpdateTransactionService updateService;
    @Autowired private DeleteTransactionService deleteService;
    @Autowired private FilterTransactionsService filterService;
    @Autowired private UserContext userContext;

    @PostMapping
    @Operation(summary = "Создать транзакцию")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest req) {
        UUID userId = userContext.getCurrentUserId();
        Transaction tx = mapToEntity(req);
        Transaction saved = createService.execute(tx, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить транзакцию по ID")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        UUID userId = userContext.getCurrentUserId();
        Transaction tx = getService.execute(id, userId);
        return ResponseEntity.ok(mapToResponse(tx));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить транзакцию")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest req) {
        UUID userId = userContext.getCurrentUserId();
        Transaction tx = mapToEntity(req);
        Transaction updated = updateService.execute(id, tx, userId);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        UUID userId = userContext.getCurrentUserId();
        deleteService.execute(id, userId);
    }

    @GetMapping
    @Operation(summary = "Список транзакций с фильтрацией и пагинацией")
    public Page<TransactionResponse> list(
            @RequestParam(required = false) UUID bankSenderId,
            @RequestParam(required = false) UUID bankReceiverId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) UUID statusId,
            @RequestParam(required = false) String receiverTin,
            @RequestParam(required = false) String minAmount,
            @RequestParam(required = false) String maxAmount,
            @RequestParam(required = false) UUID transactionTypeId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID userId = userContext.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> raw = filterService.execute(
                bankSenderId,
                bankReceiverId,
                parseDate(fromDate),
                parseDate(toDate),
                statusId,
                receiverTin,
                parseDecimal(minAmount),
                parseDecimal(maxAmount),
                transactionTypeId,
                categoryId,
                userId,
                pageable
        );
        return raw.map(this::mapToResponse);
    }

    private Transaction mapToEntity(TransactionRequest req) {
        Transaction tx = new Transaction();
        tx.setPartyType(new PartyType(req.getPartyTypeId(), null));
        tx.setTransactionType(new TransactionType(req.getTransactionTypeId(), null));
        tx.setStatus(new Status(req.getStatusId(), null));
        tx.setBankSender(new Bank(req.getBankSenderId(), null));
        tx.setBankReceiver(new Bank(req.getBankReceiverId(), null));
        tx.setAccountSender(req.getAccountSender());
        tx.setAccountReceiver(req.getAccountReceiver());
        tx.setCategory(new Category(req.getCategoryId(), null));
        tx.setReceiverTin(req.getReceiverTin());
        tx.setReceiverPhone(req.getReceiverPhone());
        tx.setComment(req.getComment());
        tx.setAmount(req.getAmount());
        tx.setTimestamp(req.getTimestamp() != null
                ? req.getTimestamp()
                : LocalDateTime.now());
        return tx;
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getCreatedByUser().getId(),    // createdByUserId
                tx.getTimestamp(),                // timestamp как LocalDateTime
                tx.getPartyType().getId(),        // partyTypeId
                tx.getTransactionType().getId(),  // transactionTypeId
                tx.getStatus().getId(),           // statusId
                tx.getBankSender().getId(),       // bankSenderId
                tx.getBankReceiver().getId(),     // bankReceiverId
                tx.getAccountSender(),            // accountSender
                tx.getAccountReceiver(),          // accountReceiver
                tx.getCategory().getId(),         // categoryId
                tx.getAmount(),                   // amount
                tx.getReceiverTin(),              // receiverTin
                tx.getReceiverPhone(),            // receiverPhone
                tx.getComment()                   // comment
        );
    }

    private LocalDateTime parseDate(String str) {
        return str != null
                ? LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
                : null;
    }

    private BigDecimal parseDecimal(String str) {
        return str != null
                ? new BigDecimal(str)
                : null;
    }
}
