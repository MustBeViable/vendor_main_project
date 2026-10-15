
package com.api_vendor.vendor_main.permissions.employee;

import com.api_vendor.vendor_main.reports.customer_orders.CustomerOrdersReportRowDTO;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/employee")
public class EmployeeReportController {

    private final EmployeeReportService employeeReportService;

    public EmployeeReportController(
            EmployeeReportService employeeReportService
    ) {
        this.employeeReportService = employeeReportService;
    }

    @GetMapping("/customer-orders")
    public Page<CustomerOrdersReportRowDTO> getCustomerOrders(
            @RequestParam Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return employeeReportService.getCustomerOrders(
                customerId,
                page,
                size
        );
    }
}
