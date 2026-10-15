
package com.api_vendor.vendor_main.reports.order_totals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderTotalsReportRowDTO(
        Integer orderId,
        Integer customerId,
        LocalDateTime orderDate,
        String status,
        BigDecimal totalQuantity,
        BigDecimal totalAmount
) {}
