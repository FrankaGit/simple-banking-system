package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.init.InitialImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InitialImportServiceTest {

    @InjectMocks
    private InitialImportService initialImportService;

    @Test
    void testImportAllTransactions(){

        List<Transaction> transactions = initialImportService.importAllTransactions("src/main/resources/transactions");

        assertEquals(100000, transactions.size());

    }
}
