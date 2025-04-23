// src/main/java/com/example/finmonitor/api/TransactionController.java
package com.example.finmonitor.api;

import com.example.finmonitor.application.dto.TransactionRequest;
import com.example.finmonitor.application.dto.TransactionResponse;
import com.example.finmonitor.application.service.CreateTransactionService;
import com.example.finmonitor.application.service.DeleteTransactionService;
import com.example.finmonitor.application.service.FilterTransactionsService;
import com.example.finmonitor.application.service.GetTransactionService;
import com.example.finmonitor.application.service.UpdateTransactionService;
import com.example.finmonitor.domain.model.*;
import com.example.finmonitor.domain.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired private CreateTransactionService createService;
    @Autowired private GetTransactionService getService;
    @Autowired private UpdateTransactionService updateService;
    @Autowired private DeleteTransactionService deleteService;
    @Autowired private FilterTransactionsService filterService;
    @Autowired private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest req,
                                                      Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        UUID userId = user.getId();

        Transaction tx = mapToEntity(req);
        Transaction saved = createService.execute(tx, userId);
        return new ResponseEntity<>(mapToResponse(saved), HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        Transaction tx = getService.execute(id);
        return ResponseEntity.ok(mapToResponse(tx));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @Validated @RequestBody TransactionRequest req
    ) {
        Transaction tx = mapToEntity(req);
        Transaction updated = updateService.execute(id, tx);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteService.execute(id);
    }

    @GetMapping
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
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> result = filterService.execute(
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
                pageable
        );
        return result.map(this::mapToResponse);
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
        tx.setAmount(req.getAmount());
        tx.setReceiverTin(req.getReceiverTin());
        tx.setReceiverPhone(req.getReceiverPhone());
        tx.setComment(req.getComment());
        tx.setTimestamp(req.getTimestamp());
        return tx;
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getCreatedByUser().getId(),
                tx.getTimestamp(),
                tx.getPartyType().getId(),
                tx.getTransactionType().getId(),
                tx.getStatus().getId(),
                tx.getBankSender().getId(),
                tx.getBankReceiver().getId(),
                tx.getAccountSender(),
                tx.getAccountReceiver(),
                tx.getCategory().getId(),
                tx.getAmount(),
                tx.getReceiverTin(),
                tx.getReceiverPhone(),
                tx.getComment()
        );
    }

    private LocalDateTime parseDate(String s) {
        return s == null ? null : LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME);
    }

    private BigDecimal parseDecimal(String s) {
        return s == null ? null : new BigDecimal(s);
    }
}
