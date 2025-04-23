package com.example.finmonitor.api;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.AuditLog;
import com.example.finmonitor.domain.repository.TransactionRepository;
import com.example.finmonitor.domain.repository.AuditLogRepository;
import com.example.finmonitor.domain.spec.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Экспорт транзакций в CSV с возможностью фильтрации.
     */
    @GetMapping(value = "/transactions", produces = "text/csv")
    public void exportTransactions(
            HttpServletResponse response,
            @RequestParam(required = false) UUID bankSenderId,
            @RequestParam(required = false) UUID bankReceiverId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) UUID statusId,
            @RequestParam(required = false) String receiverTin,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) UUID transactionTypeId,
            @RequestParam(required = false) UUID categoryId
    ) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");
        try (PrintWriter writer = response.getWriter()) {
            // Заголовок CSV
            writer.println("id,createdByUserId,timestamp,partyType,transactionType,status,bankSender,bankReceiver,accountSender,accountReceiver,category,amount,receiverTin,receiverPhone,comment");

            // Спецификация фильтрации
            Specification<Transaction> spec = TransactionSpecification.withFilters(
                    bankSenderId,
                    bankReceiverId,
                    parseDateTime(fromDate),
                    parseDateTime(toDate),
                    statusId,
                    receiverTin,
                    minAmount,
                    maxAmount,
                    transactionTypeId,
                    categoryId
            );

            // Получаем все записи
            List<Transaction> list = transactionRepository.findAll(spec);
            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;

            // Записываем строки CSV
            for (Transaction tx : list) {
                writer.printf(
                        "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s%n",
                        tx.getId(),
                        tx.getCreatedByUser().getId(),
                        tx.getTimestamp().format(fmt),
                        tx.getPartyType().getName(),
                        tx.getTransactionType().getName(),
                        tx.getStatus().getName(),
                        tx.getBankSender().getName(),
                        tx.getBankReceiver().getName(),
                        tx.getAccountSender(),
                        tx.getAccountReceiver(),
                        tx.getCategory().getName(),
                        tx.getAmount(),
                        tx.getReceiverTin(),
                        tx.getReceiverPhone(),
                        tx.getComment() != null ? tx.getComment().replaceAll(",", " ") : ""
                );
            }
        }
    }

    /**
     * Экспорт записей аудита в CSV.
     */
    @GetMapping(value = "/audit", produces = "text/csv")
    public void exportAudit(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=audit_log.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("id,entityName,entityId,changedByUserId,timestamp,changes");
            List<AuditLog> logs = auditLogRepository.findAll();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;
            for (AuditLog log : logs) {
                writer.printf(
                        "%s,%s,%s,%s,%s,%s%n",
                        log.getId(),
                        log.getEntityName(),
                        log.getEntityId(),
                        log.getChangedByUser().getId(),
                        log.getTimestamp().format(fmt),
                        log.getChanges().replaceAll(",", " ")
                );
            }
        }
    }

    private LocalDateTime parseDateTime(String s) {
        return s == null ? null : LocalDateTime.parse(s);
    }
}
