package com.cvetko.franka.simplepayment.service.implementation.init;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.CustomerService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class InitDataService {
    @Autowired
    CustomerService customerService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;


    public void createDummyCustomer() {

        try {
            Customer c = Customer
                    .builder()
                    .name("Franka Cvetko")
                    .address("Aleja Tišine 9")
                    .email("franka.cvetko@gmail.com")
                    .accounts(new ArrayList<>() {
                    })
                    .build();
            Account account = accountService.getAccountByNumber("00345");
            c.addAccount(account);
            Customer customer = customerService.saveCustomer(c);
            account.setCustomer(customer);
            accountService.saveAccount(account);

            Customer c1 = Customer
                    .builder()
                    .name("Netko Drugi")
                    .address("Ilica")
                    .email("frankacvetko12@gmail.com")
                    .accounts(new ArrayList<>())
                    .build();
            account = accountService.getAccountByNumber("00651");
            c1.addAccount(account);
            Customer customer1 = customerService.saveCustomer(c1);
            account.setCustomer(customer1);
            accountService.saveAccount(account);

            //update turnovers and balances
            accountService.getAllAccounts()
                    .parallelStream()
                    .forEach(acc -> {
                        accountService.getLastMonthTurnOver(acc, transactionService);
                        accountService.updateBalance(acc, transactionService);
                    });

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
