package com.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Customer")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_id_seq", initialValue = 1000000, allocationSize = 1)
    @Column(name = "customer_id", length = 7, nullable = false, unique = true)
    private String customerId;

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
}