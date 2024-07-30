package com.cvetko.franka.simplepayment.service.implementation;

import com.cvetko.franka.simplepayment.dao.TransactionRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.EmailService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    public static final String FILTER_NAME = "SIDE";
    public static final String FILTER_SENDER = "SENDER";
    public static final String FILTER_RECEIVER = "RECEIVER";

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountService accountService;

    @Override
    @Transactional
    public Integer processTransaction(Transaction transaction) {
        Transaction trans = transactionRepository.save(transaction);
        Account sender = accountService.findByAccountNumber(transaction.getSenderAccount()).get();
        Account receiver = accountService.findByAccountNumber(transaction.getReceiverAccount()).get();
        updateBalances(transaction, sender, receiver);
        handleBothEmails(transaction, sender, receiver);
        return trans.getTransactionId();
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public Optional<List<Transaction>> getTransactionHistory(Customer customer) {
        List<Transaction> transactions = new ArrayList<>();
        customer.getAccounts().forEach(account -> transactions.addAll(transactionRepository.getTransactionHistory(account.getAccountNumber())));

        System.out.println(transactions.size());
        return Optional.of(transactions);
    }

    @Override
    public Optional<List<Transaction>> getTransactionHistoryFiltered(Customer customer, String filter_name, String filter_value) {
        if (filter_name.equalsIgnoreCase(FILTER_NAME)) {
            switch (filter_value.toUpperCase()) {
                case FILTER_SENDER:
                    return fetchSenderTransactions(customer);
                case FILTER_RECEIVER:
                    return fetchReceiverTransactions(customer);
                default:
            }
        }
        return Optional.of(new ArrayList<>());
    }

    @Override
    public List<Transaction> fetchReceiverTransactionsForAccount(String account) {
        return transactionRepository.findByReceiverAccount(account);
    }

    @Override
    public List<Transaction> fetchSenderTransactionsForAccount(String account) {
        return transactionRepository.findBySenderAccount(account);
    }

    @Override
    public Optional<List<Transaction>> fetchSenderTransactions(Customer customer) {
        List<Transaction> transactions = new ArrayList<>();
        customer.getAccounts().forEach(account -> transactions.addAll(transactionRepository.findBySenderAccount(account.getAccountNumber())));

        return Optional.of(transactions);
    }

    @Override
    public Optional<List<Transaction>> fetchReceiverTransactions(Customer customer) {
        List<Transaction> transactions = new ArrayList<>();
        customer.getAccounts().forEach(account -> transactions.addAll(transactionRepository.findByReceiverAccount(account.getAccountNumber())));

        return Optional.of(transactions);
    }
    @Override
    public List<Transaction> fetchAllTransactionsForAccount(Account account) {
        return transactionRepository.getTransactionHistory(account.getAccountNumber());
    }


    private void handleBothEmails(Transaction transaction, Account sender, Account receiver) {
        emailService.sendEmailImpl(transaction, sender.getAccountNumber(), sender.getCustomer(), true, sender.getBalance());
        emailService.sendEmailImpl(transaction, receiver.getAccountNumber(), receiver.getCustomer(), false, receiver.getBalance());
    }

    private void updateBalances(Transaction transaction, Account sender, Account receiver) {
        sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transaction.getAmount()));
        accountService.save(sender);
        accountService.save(receiver);

    }


}
