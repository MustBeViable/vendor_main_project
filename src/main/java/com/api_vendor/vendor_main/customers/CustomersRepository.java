package com.api_vendor.vendor_main.customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepository extends JpaRepository<Customers, Integer> {

    Page<Customers> findByFirstNameContainingIgnoreCase(
            String firstName,
            Pageable pageable
    );
}