package com.cvetko.franka.simplepayment.service.implementation.init;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Currency;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class InitialImportService implements Runnable {

    private static final int THREAD_POOL_SIZE = 10;
    private static final String FILE_PATH = "src/main/resources/transactions";

    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;
    @Autowired
    InitDataService initDataService;

    @Override
    public void run() {
        readTransactionsFromCsv();
    }

    public void readTransactionsFromCsv() {

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        List<Transaction> transactions = importAllTransactions(FILE_PATH);
        CountDownLatch latch = new CountDownLatch(transactions.size());

        for (Transaction transaction : transactions) {
            executorService.submit(() -> {
                try {
                    transactionService.saveTransaction(transaction);
                    accountService.saveAccountIfNew(Account.builder()
                            .accountNumber(transaction.getSenderAccount())
                            .build());
                    accountService.saveAccountIfNew(Account.builder()
                            .accountNumber(transaction.getSenderAccount())
                            .build());
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        executorService.shutdown();

        initDataService.createDummyCustomer();


    }

    public List<Transaction> importAllTransactions(String csvFile) {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                Transaction transaction = new Transaction(
                        data[1].trim(), // senderAccountId
                        data[2].trim(), // receiverAccountId
                        new BigDecimal(data[3].trim()), // amount
                        Currency.valueOf(data[4].trim()), // currency
                        data[5].trim(), // message
                        Date.valueOf(data[6].trim()) // timestamp
                );
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle IOException
        }

        return transactions;
    }

}
