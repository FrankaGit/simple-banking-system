package com.cvetko.franka.simplepayment.dao;

import com.cvetko.franka.simplepayment.model.dto.AccountDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountDtoRepository extends JpaRepository<AccountDTO, Integer> {
    Optional<AccountDTO> findByAccountNumber(String accountNumber);
}
