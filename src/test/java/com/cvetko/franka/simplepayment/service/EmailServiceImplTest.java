package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;
import com.cvetko.franka.simplepayment.service.implementation.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Customer testCustomer;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setEmail("test@example.com");

        testTransaction = new Transaction();
        testTransaction.setTransactionId(123456);
        testTransaction.setAmount(new BigDecimal("100"));
    }

    @Test
    void testSendEmail() {
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessageMock);

        emailService.sendEmail("test@example.com", "Test Subject", "Test Body");

        verify(emailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailImpl_SentTransaction(){
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessageMock);

        BigDecimal currentBalance = new BigDecimal("500");

        emailService.sendEmailImpl(testTransaction, "123456789", testCustomer, true, currentBalance);

        verify(emailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailImpl_AddedTransaction(){
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessageMock);

        BigDecimal currentBalance = new BigDecimal("500");

        emailService.sendEmailImpl(testTransaction, "123456789", testCustomer, false, currentBalance);

        verify(emailSender).send(any(MimeMessage.class));
    }
}
