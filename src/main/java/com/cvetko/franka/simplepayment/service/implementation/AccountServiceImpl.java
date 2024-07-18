package com.cvetko.franka.simplepayment.service.implementation;

import com.cvetko.franka.simplepayment.dao.AccountRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;


    @Override
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountName(accountNumber);
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);

    }

    @Override
    public void saveAccountIfNew(Account account) {
        if (Objects.isNull(accountRepository.findByAccountName(account.getAccountNumber()))) {
            accountRepository.save(account);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public BigDecimal getLastMonthTurnOver(Account account, TransactionService transactionService) {
        BigDecimal turnover = new BigDecimal(0);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1); //first day of this month
        calendar.add(Calendar.MONTH, -1); //first day of last month
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date firstDayOfLastMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date lastDayOfLastMonth = calendar.getTime();

        List<Transaction> transactionsReceiver = transactionService.fetchReceiverTransactionsForAccount(account.getAccountNumber())
                .stream()
                .filter(transaction -> {
                    Date date = transaction.getTimestamp();
                    return date != null && !date.before(firstDayOfLastMonth) && !date.after(lastDayOfLastMonth);
                })
                .collect(Collectors.toList());

        List<Transaction> transactionsSender = transactionService.fetchSenderTransactionsForAccount(account.getAccountNumber())
                .stream()
                .filter(transaction -> {
                    Date date = transaction.getTimestamp();
                    return date != null && !date.before(firstDayOfLastMonth) && !date.after(lastDayOfLastMonth);
                })
                .collect(Collectors.toList());

        for (Transaction tran : transactionsReceiver) {
            turnover = turnover.add(tran.getAmount());
        }
        for (Transaction tran : transactionsSender) {
            turnover = turnover.subtract(tran.getAmount());
        }
        account.setPastMonthTurnover(turnover);
        accountRepository.save(account);

        return turnover;
    }

    @Override
    public void updateBalance(Account acc, TransactionService transactionService) {
        BigDecimal balance = new BigDecimal(0);
        for (Transaction t : transactionService.fetchReceiverTransactionsForAccount(acc.getAccountNumber())) {
            balance = balance.add(t.getAmount());
        }
        for (Transaction t : transactionService.fetchSenderTransactionsForAccount(acc.getAccountNumber())) {
            balance = balance.subtract(t.getAmount());
        }
        acc.setBalance(balance);
        accountRepository.save(acc);
    }

}
