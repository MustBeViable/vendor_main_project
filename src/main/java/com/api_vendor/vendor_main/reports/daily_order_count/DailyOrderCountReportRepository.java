package com.api_vendor.vendor_main.reports.daily_order_count;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyOrderCountReportRepository
        extends JpaRepository<DailyOrderCountReport, LocalDate> {

    Page<DailyOrderCountReport> findByDateBetween(
            LocalDate from,
            LocalDate to,
            Pageable pageable
    );
}
