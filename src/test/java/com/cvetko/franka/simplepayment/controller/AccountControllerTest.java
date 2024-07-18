package com.cvetko.franka.simplepayment.controller;


import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private List<Account> mockAccounts;

    @BeforeEach
    void setUp() {
        mockAccounts = Arrays.asList(
                new Account(1, "00123"),
                new Account(2, "00321")
        );
    }

    @Test
    void testGetAccounts() {
        when(accountService.getAllAccounts()).thenReturn(mockAccounts);

        ResponseEntity<List<Account>> responseEntity = accountController.getAccounts();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockAccounts);
    }
}
