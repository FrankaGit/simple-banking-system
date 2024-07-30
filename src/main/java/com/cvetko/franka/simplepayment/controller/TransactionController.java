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
    public ResponseEntity<?> getTransactionHistory(@Valid @PathVariable Integer customerId,
                                                                   @RequestParam(required = false, name = FILTER_NAME) String filterValue) {

        Optional<Customer> customerById = customerService.findCustomerById(customerId);

        if (customerById.isPresent()) {
            List<Transaction> transactionHistory;
            if (filterValue != null) {
                transactionHistory = transactionService.getTransactionHistoryFiltered(customerById.get(), FILTER_NAME, filterValue);
            } else {
                transactionHistory = transactionService.getTransactionHistory(customerById.get());
            }
            return ResponseEntity.ok(transactionHistory);
        } else {
            String errorMessage = "Customer not found with ID: " + customerId;
            return ResponseEntity.status(404).body(errorMessage);
        }
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
