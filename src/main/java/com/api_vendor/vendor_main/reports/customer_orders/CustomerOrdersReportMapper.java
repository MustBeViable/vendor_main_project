package com.api_vendor.vendor_main.reports.customer_orders;

public class CustomerOrdersReportMapper {

    public static CustomerOrdersReportRowDTO toDTO(
            CustomerOrdersReport report
    ) {
        return new CustomerOrdersReportRowDTO(
                report.getOrderId(),
                report.getCustomerId(),
                report.getFirstName(),
                report.getLastName(),
                report.getOrderDate(),
                report.getOrderStatus(),
                report.getProductId(),
                report.getProductName(),
                report.getQuantity(),
                report.getUnitPrice(),
                report.getLineTotal()
        );
    }
}
