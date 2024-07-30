package com.cvetko.franka.simplepayment.service.interfaces;

import com.cvetko.franka.simplepayment.model.dto.AccountDTO;

import java.util.Optional;

public interface AccountDtoService {
    void saveAll(Iterable<AccountDTO> accounts);
    void save(AccountDTO account);
    Optional<AccountDTO> findByAccountNumber(String accountNumber);
}
