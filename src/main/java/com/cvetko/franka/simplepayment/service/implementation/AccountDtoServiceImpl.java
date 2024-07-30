package com.cvetko.franka.simplepayment.service.implementation;

import com.cvetko.franka.simplepayment.dao.AccountDtoRepository;
import com.cvetko.franka.simplepayment.model.dto.AccountDTO;
import com.cvetko.franka.simplepayment.service.interfaces.AccountDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountDtoServiceImpl implements AccountDtoService {
    @Autowired
    AccountDtoRepository accountDtoRepository;

    @Override
    public void saveAll(Iterable<AccountDTO> accounts) {
        try {
            accountDtoRepository.saveAll(accounts);
        } catch (Exception e) {
            System.out.println("Exception while saving accounts " + e.getMessage());
        }
    }

    @Override
    public void save(AccountDTO account) {
        try {
            accountDtoRepository.save(account);
        } catch (Exception e) {
            System.out.println("Exception while saving account " + e.getMessage());
        }
    }

    @Override
    public Optional<AccountDTO> findByAccountNumber(String accountNumber) {
        try {
            return accountDtoRepository.findByAccountNumber(accountNumber);
        } catch (Exception e){
            System.out.println("Exception while finding account by account number");
            return Optional.empty();
        }
    }
}
