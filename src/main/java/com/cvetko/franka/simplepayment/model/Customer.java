package com.cvetko.franka.simplepayment.model;

import com.cvetko.franka.simplepayment.model.dto.AccountDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;
    private String name;
    private String address;
    @Email(message = "Email should be valid")
    private String email;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonManagedReference
    List<AccountDTO> accounts = new ArrayList<>();

    public void addAccount(AccountDTO account) {
        accounts.add(account);
    }
}
