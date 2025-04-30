package com.example.finmonitor.api.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными транзакции
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private UUID createdByUserId;
    private LocalDateTime timestamp;
    private UUID partyTypeId;
    private UUID transactionTypeId;
    private UUID statusId;
    private UUID bankSenderId;
    private UUID bankReceiverId;
    private String accountSender;
    private String accountReceiver;
    private UUID categoryId;
    private BigDecimal amount;
    private String receiverTin;
    private String receiverPhone;
    private String comment;
}