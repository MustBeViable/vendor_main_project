
package com.api_vendor.vendor_main.reports.order_totals;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/order-totals")
public class OrderTotalsController {

    private final OrderTotalsReportService orderTotalsReportService;

    public OrderTotalsController(
            OrderTotalsReportService orderTotalsReportService
    ) {
        this.orderTotalsReportService = orderTotalsReportService;
    }

    @GetMapping
    public Page<OrderTotalsReportRowDTO> getByCustomerId(
            @RequestParam Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return orderTotalsReportService.getReportsByCustomerId(
                customerId, page, size
        );
    }
}
