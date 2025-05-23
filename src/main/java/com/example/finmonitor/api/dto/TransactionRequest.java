package com.example.finmonitor.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для создания и обновления транзакции
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private UUID createdByUserId;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private UUID partyTypeId;

    @NotNull
    private UUID transactionTypeId;

    @NotNull
    private UUID statusId;

    @NotNull
    private UUID bankSenderId;

    @NotNull
    private UUID bankReceiverId;

    @NotBlank
    @Size(max = 34)
    private String accountSender;

    @NotBlank
    @Size(max = 34)
    private String accountReceiver;

    @NotNull
    private UUID categoryId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;

    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "\\d{11}", message = "ИНН должен состоять из 11 цифр")
    private String receiverTin;

    @NotBlank
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Телефон должен быть в формате 8XXXXXXXXXX или +7XXXXXXXXXX")
    private String receiverPhone;

    private String comment;
}