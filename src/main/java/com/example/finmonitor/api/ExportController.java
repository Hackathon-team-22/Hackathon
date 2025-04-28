package com.example.finmonitor.api;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.AuditLog;
import com.example.finmonitor.domain.repository.TransactionRepository;
import com.example.finmonitor.domain.repository.AuditLogRepository;
import com.example.finmonitor.domain.spec.AuditLogSpecification;
import com.example.finmonitor.domain.spec.TransactionSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/export")
@Tag(name = "Export", description = "Экспорт данных в CSV")
@SecurityRequirement(name = "BearerAuth")
public class ExportController {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private UserContext userContext;

    @GetMapping(value = "/transactions", produces = "text/csv")
    @Operation(summary = "Экспорт транзакций в CSV",
            description = "Фильтрация доступна по ID банков, статусу, дате, сумме и т.д.",
            responses = @ApiResponse(responseCode = "200", description = "CSV-файл с транзакциями",
                    content = @Content(mediaType = "text/csv"))
    )
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
            UUID userId = userContext.getCurrentUserId();
            writer.println("id,createdByUserId,timestamp,partyType,transactionType,status,bankSender,bankReceiver,accountSender,accountReceiver,category,amount,receiverTin,receiverPhone,comment");
            Specification<Transaction> spec = TransactionSpecification.withFilters(
                    bankSenderId, bankReceiverId, parseDateTime(fromDate), parseDateTime(toDate),
                    statusId, receiverTin, minAmount, maxAmount, transactionTypeId, categoryId, userId
            );
            List<Transaction> list = transactionRepository.findAll(spec);
            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;
            for (Transaction tx : list) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s%n",
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

    @GetMapping(value = "/audit", produces = "text/csv")
    @Operation(summary = "Экспорт логов изменений текущего пользователя")
    public void exportAudit(HttpServletResponse response) throws IOException {
        UUID userId = userContext.getCurrentUserId();
        Specification<AuditLog> spec = AuditLogSpecification.forUser(userId);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=audit_log.csv");
        try (PrintWriter writer = response.getWriter()) {
            // шапка CSV
            List<AuditLog> logs = auditLogRepository.findAll(spec);
            // запись строк
        }
    }

    private LocalDateTime parseDateTime(String s) {
        return s == null ? null : LocalDateTime.parse(s);
    }
}