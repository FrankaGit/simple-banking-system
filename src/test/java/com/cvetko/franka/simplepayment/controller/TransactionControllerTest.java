package com.cvetko.franka.simplepayment.controller;

import com.cvetko.franka.simplepayment.model.Currency;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.CustomerServiceImpl;
import com.cvetko.franka.simplepayment.service.implementation.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @Mock
    private CustomerServiceImpl customerService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void testGetTransactionHistory_CustomerFoundWithFilter() {
        Integer customerId = 1;
        String filterValue = "value";
        Customer customer = new Customer(customerId,"John Doe","Ilica 1","random@random.com",new ArrayList<>());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1,"00456","00123",new BigDecimal(2000), Currency.EUR,"message", LocalDate.now()));
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(transactionService.getTransactionHistoryFiltered(customer, TransactionController.FILTER_NAME, filterValue))
                .thenReturn(transactions);

        ResponseEntity<?> response = transactionController.getTransactionHistory(customerId, filterValue);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    public void testGetTransactionHistory_CustomerFoundWithoutFilter() {
        Integer customerId = 1;
        Customer customer = new Customer(customerId,"John Doe","Ilica 1","random@random.com",new ArrayList<>());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1,"00456","00123",new BigDecimal(2000), Currency.EUR,"message", LocalDate.now()));

        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(transactionService.getTransactionHistory(customer)).thenReturn(transactions);

        ResponseEntity<?> response = transactionController.getTransactionHistory(customerId, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    public void testGetTransactionHistory_CustomerNotFound() {
        // Setup
        Integer customerId = 1;
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = transactionController.getTransactionHistory(customerId, null);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found with ID: " + customerId, response.getBody());
    }

    @Test
    public void testProcessTransaction_Success() {
        Transaction transaction = new Transaction(1,"00456","00123",new BigDecimal(2000), Currency.EUR,"message", LocalDate.now());
        Integer result = 123;
        when(transactionService.processTransaction(transaction)).thenReturn(result);

        ResponseEntity<String> response = transactionController.processTransaction(transaction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result.toString(), response.getBody());
    }

    @Test
    public void testProcessTransaction_Failure() {
        Transaction transaction = new Transaction(1,"00456","00123",new BigDecimal(2000), Currency.EUR,"message", LocalDate.now());
        when(transactionService.processTransaction(transaction)).thenReturn(null);

        ResponseEntity<String> response = transactionController.processTransaction(transaction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Transaction processing failed", response.getBody());
    }
}

