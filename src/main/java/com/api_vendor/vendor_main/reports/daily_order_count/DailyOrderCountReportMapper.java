package com.api_vendor.vendor_main.reports.daily_order_count;

public class DailyOrderCountReportMapper {

    public static DailyOrderCountReportRowDTO toDTO(
            DailyOrderCountReport report
    ) {
        return new DailyOrderCountReportRowDTO(
                report.getDate(),
                report.getOrderCount()
        );
    }
}
