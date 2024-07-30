package com.cvetko.franka.simplepayment.config;

import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;

/*    @Scheduled(cron = "0 0 0 1 * *")
    public void task1() {
        accountService.getAllAccounts().forEach(account -> accountService.getLastMonthTurnOver(account, transactionService));
    }*/
}
