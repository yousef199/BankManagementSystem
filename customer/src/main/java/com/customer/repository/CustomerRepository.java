package com.customer.repository;

import com.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author YQadous
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
