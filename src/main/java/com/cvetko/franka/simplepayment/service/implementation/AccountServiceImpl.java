package com.cvetko.franka.simplepayment.service.implementation;

import com.cvetko.franka.simplepayment.dao.AccountRepository;
import com.cvetko.franka.simplepayment.model.Account;
import com.cvetko.franka.simplepayment.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void saveAll(Iterable<Account> accounts) {
        try {
            accountRepository.saveAll(accounts);
        } catch (Exception e) {
            System.out.println("Exception while saving accounts " + e.getMessage());
        }
    }

    @Override
    public List<Account> findAll() {
        try {
            return accountRepository.findAll();
        } catch (Exception e) {
            System.out.println("Exception while finding accounts " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Account account) {
        try {
            accountRepository.save(account);
        } catch (Exception e) {
            System.out.println("Exception while saving account " + e.getMessage());
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            return accountRepository.findByAccountNumber(accountNumber);
        } catch (Exception e) {
            System.out.println("Exception while finding account by account number");
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

}
