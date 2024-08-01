package com.cvetko.franka.simplepayment.service.interfaces;

import com.cvetko.franka.simplepayment.model.Account;

import java.util.List;
import java.util.Optional;


public interface AccountService {

    void saveAll(Iterable<Account> accounts);

    List<Account> findAll();

    void save(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> getAllAccounts();

}
