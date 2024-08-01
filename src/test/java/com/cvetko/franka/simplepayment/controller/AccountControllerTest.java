package com.cvetko.franka.simplepayment.controller;


import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    public void testGetAccounts_NoContent() {
        when(accountService.getAllAccounts()).thenReturn(new ArrayList<>());
        ResponseEntity<List<Account>> response = accountController.getAccounts();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetAccounts_Ok() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "Account1"));
        accounts.add(new Account(2, "Account2"));
        when(accountService.getAllAccounts()).thenReturn(accounts);
        ResponseEntity<List<Account>> response = accountController.getAccounts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accounts, response.getBody());
    }

    @Test
    public void testGetAccounts_InternalServerError() {
        when(accountService.getAllAccounts()).thenThrow(new RuntimeException("Exception"));
        ResponseEntity<List<Account>> response = accountController.getAccounts();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
