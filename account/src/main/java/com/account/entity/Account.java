package com.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @Column(name = "account_id", length = 10, nullable = false)
    private Integer accountId;

    @Column(name = "customer_id", length = 7, nullable = false)
    private Integer customerId;

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "account_type", length = 20, nullable = false)
    private String accountType;

    @Column(name = "account_status", length = 20, nullable = false)
    private String accountStatus;
}
