package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.implementation.init.InitDataService;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.CustomerService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitDataServiceTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private InitDataService initDataService;

    private Account account1;
    private Account account2;
    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        account1 = new Account();
        account1.setAccountNumber("00345");

        account2 = new Account();
        account2.setAccountNumber("00651");

        customer1 = Customer.builder()
                .name("Franka Cvetko")
                .address("Aleja Tišine 9")
                .email("franka.cvetko@gmail.com")
                .accounts(new ArrayList<>())
                .build();

        customer2 = Customer.builder()
                .name("Netko Drugi")
                .address("Ilica")
                .email("frankacvetko12@gmail.com")
                .accounts(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateDummyCustomer(){
        when(accountService.getAccountByNumber("00345")).thenReturn(account1);
        when(accountService.getAccountByNumber("00651")).thenReturn(account2);
        when(customerService.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(account1, account2));

        initDataService.createDummyCustomer();

        verify(accountService).getAccountByNumber("00345");
        verify(accountService).getAccountByNumber("00651");

        verify(customerService, times(2)).saveCustomer(any(Customer.class));
        verify(accountService, times(2)).saveAccount(any(Account.class));

        verify(accountService).getAllAccounts();
        verify(accountService, times(2)).getLastMonthTurnOver(any(Account.class), eq(transactionService));
    }
}

