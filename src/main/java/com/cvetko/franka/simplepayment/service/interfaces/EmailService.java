package com.cvetko.franka.simplepayment.service.interfaces;

import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.model.Transaction;

import java.math.BigDecimal;

public interface EmailService {

    void sendEmailImpl(Transaction transaction, String accountNumber, Customer customer, BigDecimal currentBalance, boolean sent);
}
