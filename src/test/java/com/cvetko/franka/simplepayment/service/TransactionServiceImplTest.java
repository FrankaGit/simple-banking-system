package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.dao.TransactionRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Currency;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.TransactionServiceImpl;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AccountService accountService;

    @Test
    @Transactional
    public void testProcessTransaction() {
        Transaction transaction = new Transaction("123", "456", BigDecimal.TEN, Currency.EUR, "Test", LocalDate.now());
        Account sender = new Account("123");
        sender.setBalance(BigDecimal.valueOf(100));
        Account receiver = new Account("456");
        receiver.setBalance(BigDecimal.valueOf(50));

        when(accountService.findByAccountNumber("123")).thenReturn(Optional.of(sender));
        when(accountService.findByAccountNumber("456")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        transactionService.processTransaction(transaction);

        verify(accountService).save(sender);
        verify(accountService).save(receiver);
        verify(emailService).sendEmailImpl(any(), eq("123"), any(), any(), eq(true));
        verify(emailService).sendEmailImpl(any(), eq("456"), any(), any(), eq(false));
    }

    @Test
    public void testSaveTransaction() {
        Transaction transaction = new Transaction("123", "456", BigDecimal.TEN, Currency.EUR, "Test", LocalDate.now());
        transactionService.saveTransaction(transaction);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    public void testGetTransactionHistory() {
        Customer customer = new Customer();
        Account account1 = new Account("123");
        Account account2 = new Account("456");
        customer.setAccounts(List.of(account1, account2));

        List<Transaction> transactions1 = List.of(new Transaction(), new Transaction());
        List<Transaction> transactions2 = List.of(new Transaction());

        when(transactionRepository.getTransactionHistory("123")).thenReturn(transactions1);
        when(transactionRepository.getTransactionHistory("456")).thenReturn(transactions2);

        List<Transaction> result = transactionService.getTransactionHistory(customer);

        assertEquals(3, result.size());
        verify(transactionRepository, times(1)).getTransactionHistory("123");
        verify(transactionRepository, times(1)).getTransactionHistory("456");
    }

    @Test
    public void testGetTransactionHistoryFiltered_Sender() {
        Customer customer = new Customer();
        Account account = new Account("123");
        customer.setAccounts(List.of(account));

        List<Transaction> transactions = List.of(new Transaction());

        when(transactionRepository.findBySenderAccount("123")).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionHistoryFiltered(customer, TransactionServiceImpl.FILTER_NAME, TransactionServiceImpl.FILTER_SENDER);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findBySenderAccount("123");
    }

    @Test
    public void testGetTransactionHistoryFiltered_Receiver() {
        Customer customer = new Customer();
        Account account = new Account("123");
        customer.setAccounts(List.of(account));

        List<Transaction> transactions = List.of(new Transaction());

        when(transactionRepository.findByReceiverAccount("123")).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionHistoryFiltered(customer, TransactionServiceImpl.FILTER_NAME, TransactionServiceImpl.FILTER_RECEIVER);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByReceiverAccount("123");
    }

    @Test
    public void testFindAllTransactionsForAccount() {

        Account account = new Account("123");
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(transactionRepository.getTransactionHistory("123")).thenReturn(transactions);

        List<Transaction> result = transactionService.findAllTransactionsForAccount(account);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).getTransactionHistory("123");
    }

}
