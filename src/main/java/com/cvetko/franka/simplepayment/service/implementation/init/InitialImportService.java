package com.cvetko.franka.simplepayment.service.implementation.init;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Currency;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.CustomerServiceImpl;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import com.cvetko.franka.simplepayment.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InitialImportService implements Runnable {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int NUMBER_OF_CUSTOMERS = 100;
    private static final String FILE_PATH = "src/main/resources/transactions";

    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;
    @Autowired
    CustomerServiceImpl customerService;

    @Override
    public void run() {
        initialDataImport();
    }

    public void initialDataImport() {
        
        List<Transaction> transactions = importAllTransactions(FILE_PATH);
        List<Customer> customers = customerService.createDummyCustomers(NUMBER_OF_CUSTOMERS);
        Set<Account> accounts = retrieveUniqueAccountsFromTransactions(transactions);

        setCustomerAccounts(customers,accounts);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(transactions.size());

        transactions.forEach(t -> executor.submit(() -> {
            try {
                processTransaction(t, accounts);
            } finally {
                latch.countDown();
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        executor.shutdown();
        accountService.saveAll(accounts);
    }


    private void processTransaction(Transaction t, Set<Account> accounts) {

        Optional<Account> sender = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(t.getSenderAccount()))
                .findFirst();

        Optional<Account> receiver = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(t.getReceiverAccount()))
                .findFirst();

        if (sender.isPresent() && receiver.isPresent()) {

            sender.get().updateBalance(t.getAmount().negate());
            receiver.get().updateBalance(t.getAmount());

            if(isWithinLastMonth(t.getTimestamp())) {
                sender.get().updateTurnover(t.getAmount().negate());
                receiver.get().updateTurnover(t.getAmount());
            }
            transactionService.saveTransaction(t);
        }
    }

    private Set<Account> retrieveUniqueAccountsFromTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .flatMap(t -> Stream.of(
                        new Account(t.getSenderAccount()),
                        new Account(t.getReceiverAccount())
                ))
                .collect(Collectors.toSet());
    }

    public List<Transaction> importAllTransactions(String csvFile) {
        List<Transaction> transactions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
                        LocalDate.parse(data[6].trim(),formatter) // timestamp
                );
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
    private void setCustomerAccounts(List<Customer> customers, Set<Account> accounts) {
        accounts.forEach(a -> a.setCustomer(RandomUtils.getRandomItem(customers)));
    }
    private boolean isWithinLastMonth(LocalDate transactionDate) {
        if (transactionDate == null) {
            return false;
        }

        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = firstDayOfLastMonth.withDayOfMonth(firstDayOfLastMonth.lengthOfMonth());

        return !transactionDate.isBefore(firstDayOfLastMonth) && !transactionDate.isAfter(lastDayOfLastMonth);
    }
}
