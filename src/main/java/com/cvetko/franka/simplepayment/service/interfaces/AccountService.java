package com.cvetko.franka.simplepayment.service.interfaces;

import com.cvetko.franka.simplepayment.model.Account;

import java.math.BigDecimal;
import java.util.List;


public interface AccountService {

    Account getAccountByNumber(String accountNumber);

    Account saveAccount(Account account);

    List<Account> getAllAccounts();

    BigDecimal getLastMonthTurnOver(Account account, TransactionService transactionService);

    void updateBalance(Account acc, TransactionService transactionService);
}
