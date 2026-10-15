package com.api_vendor.vendor_main.reports.daily_order_count;

import java.time.LocalDate;

public record DailyOrderCountReportRowDTO(
        LocalDate date,
        Integer orderCount
) {}
