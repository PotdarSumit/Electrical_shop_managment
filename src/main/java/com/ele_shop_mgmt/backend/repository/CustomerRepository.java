package com.ele_shop_mgmt.backend.repository;

import com.ele_shop_mgmt.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByMobileNumber(String mobileNumber);
    List<Customer> findByNameContainingIgnoreCase(String keyword);
}
