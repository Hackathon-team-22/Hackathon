package com.example.finmonitor.api;

import com.example.finmonitor.application.service.TransactionService;
import com.example.finmonitor.domain.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public Transaction create(@RequestBody Transaction tx) {
        return transactionService.create(tx);
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable UUID id) {
        return transactionService.getById(id);
    }

    @PutMapping("/{id}")
    public Transaction update(@PathVariable UUID id, @RequestBody Transaction tx) {
        return transactionService.update(id, tx);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }

    @GetMapping
    public Page<Transaction> list(
            @RequestParam(name = "bankSender", required = false) UUID bankSenderId,
            @RequestParam(name = "bankReceiver", required = false) UUID bankReceiverId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(name = "status", required = false) UUID statusId,
            @RequestParam(name = "tin", required = false) String receiverTin,
            @RequestParam(name = "amountMin", required = false) BigDecimal amountMin,
            @RequestParam(name = "amountMax", required = false) BigDecimal amountMax,
            @RequestParam(name = "type", required = false) UUID transactionTypeId,
            @RequestParam(name = "category", required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionService.list(
                bankSenderId,
                bankReceiverId,
                dateFrom,
                dateTo,
                statusId,
                receiverTin,
                amountMin,
                amountMax,
                transactionTypeId,
                categoryId,
                pageable
        );
    }
}
