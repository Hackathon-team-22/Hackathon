package com.example.finmonitor.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "party_type_id", nullable = false)
    private PartyType partyType;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "bank_sender_id", nullable = false)
    private Bank bankSender;

    @ManyToOne
    @JoinColumn(name = "bank_receiver_id", nullable = false)
    private Bank bankReceiver;

    @Column(name = "account_sender", nullable = false)
    private String accountSender;

    @Column(name = "account_receiver", nullable = false)
    private String accountReceiver;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 18, scale = 5)
    private BigDecimal amount;

    @Column(name = "receiver_tin", nullable = false, length = 11)
    private String receiverTin;

    @Column(name = "receiver_phone", nullable = false, length = 12)
    private String receiverPhone;

    @Column(columnDefinition = "TEXT")
    private String comment;
}