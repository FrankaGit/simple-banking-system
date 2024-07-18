package com.cvetko.franka.simplepayment.controller;

import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.CustomerServiceImpl;
import com.cvetko.franka.simplepayment.service.implementation.TransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController {

    public static final String FILTER_NAME = "SIDE";
    @Autowired
    TransactionServiceImpl transactionService;
    @Autowired
    CustomerServiceImpl customerService;

    @GetMapping("/transaction/history/{customerId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@Valid @PathVariable Integer customerId,
                                                                   @RequestParam(required = false, name = FILTER_NAME) String filterValue) {

        Optional<List<Transaction>> transactionHistory = Optional.empty();
        Optional<Customer> customerById = customerService.findCustomerById(customerId);

        if (filterValue != null && customerById.isPresent()) {
            transactionHistory = transactionService.getTransactionHistoryFiltered(customerById.get(), FILTER_NAME, filterValue);
        } else if (customerById.isPresent()) {
            transactionHistory = transactionService.getTransactionHistory(customerById.get());
        }

        return transactionHistory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/transaction")
    public ResponseEntity<String> processTransaction(@RequestBody Transaction transaction) {

        Integer result = transactionService.processTransaction(transaction);
        if (result != null) {
            return ResponseEntity.ok(result.toString());
        } else {
            return ResponseEntity.status(500).body("Transaction processing failed");
        }
    }
}
