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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        given(accountRepository.findByAccountName("123456")).willReturn(account);
        Account result = accountServiceImpl.getAccountByNumber("123456");
        assertThat(result).isEqualTo(account);
        verify(accountRepository).findByAccountName("123456");
    }

    @Test
    void testSaveAccount() {
        given(accountRepository.save(account)).willReturn(account);
        Account result = accountServiceImpl.saveAccount(account);
        assertThat(result).isEqualTo(account);
        verify(accountRepository).save(account);
    }

    @Test
    void testSaveAccountIfNew() {
        given(accountRepository.findByAccountName("123456")).willReturn(null);
        accountServiceImpl.saveAccountIfNew(account);
        verify(accountRepository).save(account);
    }

    @Test
    void testGetAllAccounts() {
        List<Account> accounts = Arrays.asList(account);
        given(accountRepository.findAll()).willReturn(accounts);
        List<Account> result = accountServiceImpl.getAllAccounts();
        assertThat(result).isEqualTo(accounts);
        verify(accountRepository).findAll();
    }

    @Test
    void testGetLastMonthTurnOver() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfLastMonth = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayOfLastMonth = calendar.getTime();

        Transaction transaction1 = new Transaction();
        transaction1.setTimestamp(firstDayOfLastMonth);
        transaction1.setAmount(new BigDecimal("100"));

        Transaction transaction2 = new Transaction();
        transaction2.setTimestamp(lastDayOfLastMonth);
        transaction2.setAmount(new BigDecimal("50"));

        given(transactionService.fetchReceiverTransactionsForAccount("123456"))
                .willReturn(Arrays.asList(transaction1));
        given(transactionService.fetchSenderTransactionsForAccount("123456"))
                .willReturn(Arrays.asList(transaction2));
        given(accountRepository.save(account)).willReturn(account);

        BigDecimal turnover = accountServiceImpl.getLastMonthTurnOver(account, transactionService);

        assertThat(turnover).isEqualByComparingTo(new BigDecimal("50"));
        assertThat(account.getPastMonthTurnover()).isEqualByComparingTo(new BigDecimal("50"));
        verify(accountRepository).save(account);
    }
    @Test
    public void testUpdateBalance() {
        List<Transaction> receiverTransactions = Arrays.asList(transaction1, transaction3);
        List<Transaction> senderTransactions = Arrays.asList(transaction2);

        when(transactionService.fetchReceiverTransactionsForAccount("123456")).thenReturn(receiverTransactions);
        when(transactionService.fetchSenderTransactionsForAccount("123456")).thenReturn(senderTransactions);

        accountServiceImpl.updateBalance(account, transactionService);

        assertEquals(new BigDecimal("250.00"), account.getBalance());

        verify(transactionService, times(1)).fetchReceiverTransactionsForAccount("123456");
        verify(transactionService, times(1)).fetchSenderTransactionsForAccount("123456");
        verify(accountRepository, times(1)).save(account);
    }
}
