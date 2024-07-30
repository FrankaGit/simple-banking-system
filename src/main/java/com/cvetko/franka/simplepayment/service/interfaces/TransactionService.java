package com.cvetko.franka.simplepayment.service.interfaces;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;

import java.util.List;

public interface TransactionService {

    void saveTransaction(Transaction transaction);

    List<Transaction> getTransactionHistory(Customer customer);

    List<Transaction> getTransactionHistoryFiltered(Customer customer, String t_side, String t_side_value);

    Integer processTransaction(Transaction transaction);

    List<Transaction> findAllTransactionsForAccount(Account account);
}
