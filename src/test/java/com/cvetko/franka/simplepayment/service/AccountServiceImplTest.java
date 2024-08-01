package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.dao.AccountRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.service.implementation.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void testSaveAll_Success() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "Account1"));
        accountService.saveAll(accounts);
        verify(accountRepository, times(1)).saveAll(accounts);
    }

    @Test
    public void testFindAll_Success() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "Account1"));
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.findAll();

        assertEquals(accounts, result);
    }

    @Test
    public void testFindAll_Exception() {
        when(accountRepository.findAll()).thenThrow(new RuntimeException("Exception"));
        List<Account> result = accountService.findAll();
        assertNull(result);
    }

    @Test
    public void testSave_Success() {
        Account account = new Account(1, "Account1");
        accountService.save(account);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testFindByAccountNumber_Success() {
        String accountNumber = "12345";
        Account account = new Account(1, accountNumber);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.findByAccountNumber(accountNumber);
        assertEquals(Optional.of(account), result);
    }

    @Test
    public void testFindByAccountNumber_Exception() {
        String accountNumber = "12345";
        when(accountRepository.findByAccountNumber(accountNumber)).thenThrow(new RuntimeException("Exception"));
        Optional<Account> result = accountService.findByAccountNumber(accountNumber);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "Account1"));
        when(accountRepository.findAll()).thenReturn(accounts);
        List<Account> result = accountService.getAllAccounts();
        assertEquals(accounts, result);
    }
}

