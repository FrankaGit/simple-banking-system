package com.cvetko.franka.simplepayment.controller;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            if (accounts.isEmpty()) return ResponseEntity.noContent().build();
            else return ResponseEntity.ok(accounts);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
