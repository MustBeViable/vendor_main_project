
package com.api_vendor.vendor_main.reports.order_totals;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTotalsReportRepository
        extends JpaRepository<OrderTotalsReport, Integer> {

    Page<OrderTotalsReport> findByCustomerId(
            Integer customerId,
            Pageable pageable
    );
}
