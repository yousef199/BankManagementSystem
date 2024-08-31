package com.account.repository;

import com.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByCustomerId(Integer customerId);
    void deleteAccountsByCustomerId(Integer customerId);

    @Modifying
    @Query("UPDATE Account a SET a.accountStatus = :newStatus WHERE a.customerId = :customerId")
    int updateAccountStatusByCustomerId(@Param("customerId") int customerId, @Param("newStatus") String newStatus);
}
