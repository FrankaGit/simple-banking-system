package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.model.Currency;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.CustomerServiceImpl;
import com.cvetko.franka.simplepayment.service.implementation.init.InitialImportService;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import com.cvetko.franka.simplepayment.service.interfaces.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitialImportServiceTest {

    @InjectMocks
    private InitialImportService initialImportService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private CustomerServiceImpl customerService;

    @Test
    public void testInitialDataImport() {
        List<Customer> customers = Arrays.asList(new Customer());
        Set<Account> accounts = new HashSet<>(Arrays.asList(new Account("123"), new Account("456")));

        when(customerService.createDummyCustomers(anyInt())).thenReturn(customers);

        initialImportService.initialDataImport();

        verify(transactionService, times(100_000)).saveTransaction(any(Transaction.class));
        verify(accountService, times(1)).saveAll(anySet());
        verify(customerService, times(1)).createDummyCustomers(anyInt());
    }

    @Test
    public void testProcessTransaction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Transaction transaction = new Transaction("123", "456", BigDecimal.TEN, Currency.EUR, "Test", LocalDate.now());
        Account sender = new Account("123");
        Account receiver = new Account("456");
        Set<Account> accounts = new HashSet<>(Arrays.asList(sender, receiver));

        Method method = InitialImportService.class.getDeclaredMethod("processTransaction", Transaction.class, Set.class);
        method.setAccessible(true);

        method.invoke(initialImportService, transaction, accounts);

        assertEquals(BigDecimal.TEN.negate(), sender.getBalance());
        assertEquals(BigDecimal.TEN, receiver.getBalance());
    }

    @Test
    public void testImportAllTransactions() throws Exception {
        Path tempFile = Files.createTempFile("transactions", ".csv");
        String content = "1,00923,00598,3584.96,EUR,ATRCTIVH JDIMSGGEJVK,2023-08-01\n";
        Files.write(tempFile, content.getBytes());

        List<Transaction> transactions = initialImportService.importAllTransactions(tempFile.toString());

        assertEquals(1, transactions.size());
        assertEquals("00923", transactions.get(0).getSenderAccount());
        assertEquals("00598", transactions.get(0).getReceiverAccount());

        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testIsWithinLastMonth() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = InitialImportService.class.getDeclaredMethod("isWithinLastMonth", LocalDate.class);
        method.setAccessible(true);

        LocalDate now = LocalDate.now();
        LocalDate fifteenDaysAgo = now.minusDays(15);
        LocalDate twoMonthsAgo = now.minusMonths(2);
        LocalDate tomorrow = now.plusDays(1);

        boolean result;

        result = (boolean) method.invoke(initialImportService, fifteenDaysAgo);
        assertTrue(result, "Expected true for date within last month");

        result = (boolean) method.invoke(initialImportService, twoMonthsAgo);
        assertFalse(result, "Expected false for date more than one month ago");

        result = (boolean) method.invoke(initialImportService, tomorrow);
        assertFalse(result, "Expected false for future date");

        result = (boolean) method.invoke(initialImportService, (Object) null);
        assertFalse(result, "Expected false for null date");
    }
}
