package com.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author YQadous
 * Customer entity
 */
@Entity
@Table(name = "Customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", allocationSize = 1)
    @Column(name = "customer_id", length = 7, nullable = false)
    private Integer customerId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "legal_id", length = 50, nullable = false, unique = true)
    private String legalId;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 150, unique = true)
    @Email
    private String email;

    @Column(name = "number_of_accounts", nullable = false)
    Integer numberOfAccounts;

    @Column(name = "customer_status", length = 20, nullable = false)
    private String customerStatus;
}