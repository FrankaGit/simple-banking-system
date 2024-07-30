package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.dao.AccountRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.AccountServiceImpl;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    private Account account;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountNumber("123456");
        account.setBalance(BigDecimal.ZERO);

        transaction1 = new Transaction();
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setReceiverAccount("ACC123");

        transaction2 = new Transaction();
        transaction2.setAmount(new BigDecimal("50.00"));
        transaction2.setSenderAccount("ACC123");

        transaction3 = new Transaction();
        transaction3.setAmount(new BigDecimal("200.00"));
        transaction3.setReceiverAccount("ACC123");
    }

    @Test
    void testGetAccountByNumber() {
        given(accountRepository.findByAccountNumber("123456").get()).willReturn(account);
        Account result = accountServiceImpl.findByAccountNumber("123456").get();
        assertThat(result).isEqualTo(account);
        verify(accountRepository).findByAccountNumber("123456");
    }

    @Test
    void testGetAllAccounts() {
        List<Account> accounts = Arrays.asList(account);
        given(accountRepository.findAll()).willReturn(accounts);
        List<Account> result = accountServiceImpl.getAllAccounts();
        assertThat(result).isEqualTo(accounts);
        verify(accountRepository).findAll();
    }


}
