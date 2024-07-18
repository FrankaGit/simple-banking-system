package com.cvetko.franka.simplepayment.dao;

import com.cvetko.franka.simplepayment.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :account")
    Account findByAccountName(@Param("account") String account);
}
