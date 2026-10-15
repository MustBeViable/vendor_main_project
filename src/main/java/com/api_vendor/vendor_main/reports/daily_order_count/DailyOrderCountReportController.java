package com.api_vendor.vendor_main.reports.daily_order_count;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports/daily-order-count")
public class DailyOrderCountReportController {

    private final DailyOrderCountReportService dailyOrderCountReportService;

    public DailyOrderCountReportController(
            DailyOrderCountReportService dailyOrderCountReportService
    ) {
        this.dailyOrderCountReportService = dailyOrderCountReportService;
    }

    @GetMapping
    public Page<DailyOrderCountReportRowDTO> getByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return dailyOrderCountReportService.getReportsByDateRange(
                from, to, page, size
        );
    }
}
