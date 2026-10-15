package com.api_vendor.vendor_main.reports.customer_orders;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerOrdersReportRowDTO(
        Integer orderId,
        Integer customerId,
        String firstName,
        String lastName,
        LocalDateTime orderDate,
        String orderStatus,
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}

