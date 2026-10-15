package com.api_vendor.vendor_main.reports.daily_order_count;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class DailyOrderCountReportService {

    private final DailyOrderCountReportRepository dailyOrderCountReportRepository;

    public DailyOrderCountReportService(
            DailyOrderCountReportRepository dailyOrderCountReportRepository
    ) {
        this.dailyOrderCountReportRepository = dailyOrderCountReportRepository;
    }

    @Transactional(readOnly = true)
    public Page<DailyOrderCountReportRowDTO> getReportsByDateRange(
            LocalDate from,
            LocalDate to,
            int page,
            int size
    ) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date range"
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("date").descending()
        );

        return dailyOrderCountReportRepository
                .findByDateBetween(from, to, pageable)
                .map(DailyOrderCountReportMapper::toDTO);
    }
}
