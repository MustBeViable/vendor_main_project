
package com.api_vendor.vendor_main.reports.order_totals;

public class OrderTotalsReportMapper {

    public static OrderTotalsReportRowDTO toDTO(
            OrderTotalsReport report
    ) {
        return new OrderTotalsReportRowDTO(
                report.getOrderId(),
                report.getCustomerId(),
                report.getOrderDate(),
                report.getStatus(),
                report.getTotalQuantity(),
                report.getTotalAmount()
        );
    }
}
