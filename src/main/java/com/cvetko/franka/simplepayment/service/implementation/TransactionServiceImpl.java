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

        Optional<Account> sender = accountService.findByAccountNumber(transaction.getSenderAccount());
        Optional<Account> receiver = accountService.findByAccountNumber(transaction.getReceiverAccount());
        if(sender.isPresent() && receiver.isPresent())
        {
            Transaction trans = transactionRepository.save(transaction);
            updateBalances(transaction, sender.get(), receiver.get());
            handleBothEmails(transaction, sender.get(), receiver.get());
            return trans.getTransactionId();
        }
        return null;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionHistory(Customer customer) {
        List<Transaction> transactions = new ArrayList<>();
        customer.getAccounts().forEach(account -> transactions.addAll(transactionRepository.getTransactionHistory(account.getAccountNumber())));

        return transactions;
    }

    @Override
    public List<Transaction> getTransactionHistoryFiltered(Customer customer, String filter_name, String filter_value) {
        if (filter_name.equalsIgnoreCase(FILTER_NAME)) {
            switch (filter_value.toUpperCase()) {
                case FILTER_SENDER:
                    return findSenderTransactions(customer);
                case FILTER_RECEIVER:
                    return findReceiverTransactions(customer);
                default:
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Transaction> findAllTransactionsForAccount(Account account) {
        return transactionRepository.getTransactionHistory(account.getAccountNumber());
    }


    private void handleBothEmails(Transaction transaction, Account sender, Account receiver) {
        emailService.sendEmailImpl(transaction, sender.getAccountNumber(), sender.getCustomer(), sender.getBalance(), true);
        emailService.sendEmailImpl(transaction, receiver.getAccountNumber(), receiver.getCustomer(), receiver.getBalance(), false);
    }

    private void updateBalances(Transaction transaction, Account sender, Account receiver) {
        sender.updateBalance(transaction.getAmount().negate());
        receiver.updateBalance(transaction.getAmount());
        accountService.save(sender);
        accountService.save(receiver);

    }

    private List<Transaction> findSenderTransactions(Customer customer) {
        List<Transaction> transactions = new ArrayList<>();
        customer.getAccounts().forEach(account -> transactions.addAll(transactionRepository.findBySenderAccount(account.getAccountNumber())));

        return transactions;
    }

    private List<Transaction> findReceiverTransactions(Customer customer) {
        List<Transaction> transactions = new ArrayList<>();
        customer.getAccounts().forEach(account -> transactions.addAll(transactionRepository.findByReceiverAccount(account.getAccountNumber())));

        return transactions;
    }


}
