
package com.api_vendor.vendor_main.reports.customer_orders;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/customer-orders")
public class CustomerOrdersController {

    private final CustomerOrdersReportService customerOrdersReportService;

    public CustomerOrdersController(
            CustomerOrdersReportService customerOrdersReportService
    ) {
        this.customerOrdersReportService = customerOrdersReportService;
    }

    @GetMapping
    public Page<CustomerOrdersReportRowDTO> getByCustomerId(
            @RequestParam Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return customerOrdersReportService.getReportsByCustomerId(
                customerId, page, size
        );
    }
}
