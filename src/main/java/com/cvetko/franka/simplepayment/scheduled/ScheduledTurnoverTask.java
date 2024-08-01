package com.cvetko.franka.simplepayment.scheduled;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduledTurnoverTask {
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void turnover() {
        calculateLastMonthTurnover();
    }

    public void calculateLastMonthTurnover() {
        List<Account> accounts = accountService.findAll();
        LocalDate firstDayOfLastMonth = returnFirstDayOfLastMonth();
        LocalDate lastDayOfLastMonth = returnLastDayOfLastMonth();

        //for each account we will get all of the transactions filter by date
        //then calculate the turnover depending on sender or receiver
        accounts.parallelStream()
                .forEach(account -> {
                    account.setPastMonthTurnover(new BigDecimal(0));
                    List<Transaction> transactions = transactionService.findAllTransactionsForAccount(account);
                    transactions = filterTransactionsByDate(transactions, firstDayOfLastMonth, lastDayOfLastMonth);

                    if (transactions != null && !transactions.isEmpty()) {
                        transactions
                                .parallelStream()
                                .forEach(transaction -> {
                                    if (transaction.getSenderAccount().equals(account.getAccountNumber())) {
                                        account.updateTurnover(transaction.getAmount().negate());
                                    } else {
                                        account.updateTurnover(transaction.getAmount());
                                    }
                                });
                    }
                });
        accountService.saveAll(accounts);
    }

    private LocalDate returnLastDayOfLastMonth() {
        LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);
        return lastDayOfLastMonth;
    }

    private LocalDate returnFirstDayOfLastMonth() {
        LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
        return firstDayOfLastMonth;
    }
    private List<Transaction> filterTransactionsByDate(List<Transaction> transactions, LocalDate firstDayOfLastMonth, LocalDate lastDayOfLastMonth) {
        return transactions
                .stream()
                .filter(transaction -> {
                    LocalDate date = transaction.getTimestamp();
                    return date != null && !date.isBefore(firstDayOfLastMonth) && !date.isAfter(lastDayOfLastMonth);
                })
                .collect(Collectors.toList());

    }
}
