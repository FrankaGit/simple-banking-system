package com.cvetko.franka.simplepayment.scheduled;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
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
        Date firstDayOfLastMonth = returnFirstDayOfLastMonth();
        Date lastDayOfLastMonth = returnLastDayOfLastMonth();

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

    private Date returnLastDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1); //first day of this month
        calendar.add(Calendar.MONTH, -1); //first day of last month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date returnFirstDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1); //first day of this month
        calendar.add(Calendar.MONTH, -1); //first day of last month
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    private List<Transaction> filterTransactionsByDate(List<Transaction> transactions, Date firstDayOfLastMonth, Date lastDayOfLastMonth) {
        return transactions
                .stream()
                .filter(transaction -> {
                    Date date = transaction.getTimestamp();
                    return date != null && !date.before(firstDayOfLastMonth) && !date.after(lastDayOfLastMonth);
                })
                .collect(Collectors.toList());

    }
}
