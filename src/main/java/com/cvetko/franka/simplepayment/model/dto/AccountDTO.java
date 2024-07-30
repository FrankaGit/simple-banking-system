package com.cvetko.franka.simplepayment.model.dto;

import com.cvetko.franka.simplepayment.model.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;
    @Column(unique = true)
    private String accountNumber;
    private BigDecimal balance = new BigDecimal(0);
    private BigDecimal pastMonthTurnover = new BigDecimal(0);
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    public AccountDTO(String accountNumber) {
        this.setAccountNumber(accountNumber);
    }

    public synchronized void updateBalance(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.balance = this.balance.add(amount);
    }
}
