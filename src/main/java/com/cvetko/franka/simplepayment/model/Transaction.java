package com.cvetko.franka.simplepayment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;
    @NotNull
    private String senderAccount;
    @NotNull
    private String receiverAccount;
    @NotNull
    private BigDecimal amount;
    @NotNull
    @Enumerated(EnumType.STRING)
    @NotNull
    private Currency currency;
    @NotNull
    private String message;
    @NotNull
    private Date timestamp;

    public Transaction(String senderAccountId, String receiverAccountId, BigDecimal amount, Currency currency, String message, Date timestamp) {
        this.senderAccount = senderAccountId;
        this.receiverAccount = receiverAccountId;
        this.amount = amount;
        this.currency = currency;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Transaction(String senderAccountId, String receiverAccountId, BigDecimal amount, Currency currency, String message) {
        this.senderAccount = senderAccountId;
        this.receiverAccount = receiverAccountId;
        this.amount = amount;
        this.currency = currency;
        this.message = message;
        this.timestamp = new Date();
    }
}
