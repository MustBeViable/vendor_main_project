package com.api_vendor.vendor_main.reports.customer_orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrdersReportRepository
        extends JpaRepository<CustomerOrdersReport, String> {
    Page<CustomerOrdersReport> findByCustomerId(
            Integer customerId,
            Pageable pageable
    );
}