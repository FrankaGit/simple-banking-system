package com.cvetko.franka.simplepayment.service.implementation;

import com.cvetko.franka.simplepayment.dao.AccountRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void saveAll(Iterable<Account> accounts) {
        try {
            accountRepository.saveAll(accounts);
        } catch (Exception e) {
            System.out.println("Exception while saving accounts " + e.getMessage());
        }
    }

    @Override
    public List<Account> findAll() {
        try {
            return accountRepository.findAll();
        } catch (Exception e) {
            System.out.println("Exception while finding accounts " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Account account) {
        try {
            accountRepository.save(account);
        } catch (Exception e) {
            System.out.println("Exception while saving account " + e.getMessage());
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            return accountRepository.findByAccountNumber(accountNumber);
        } catch (Exception e) {
            System.out.println("Exception while finding account by account number");
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }


    @Override
    public void calculateLastMonthTurnover(TransactionService transactionService) {
        List<Account> accounts = findAll();
        Date firstDayOfLastMonth = returnFirstDayOfLastMonth();
        Date lastDayOfLastMonth = returnLastDayOfLastMonth();

        //for each account we will get all of the transactions filter by date
        //then calculate the turnover depending on sender or receiver
        accounts.parallelStream()
                .forEach(account -> {
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
        accountRepository.saveAll(accounts);
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


}
