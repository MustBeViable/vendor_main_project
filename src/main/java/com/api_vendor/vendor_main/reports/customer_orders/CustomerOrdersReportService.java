
package com.api_vendor.vendor_main.reports.customer_orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerOrdersReportService {

    private final CustomerOrdersReportRepository customerOrdersReportRepository;

    public CustomerOrdersReportService(
            CustomerOrdersReportRepository customerOrdersReportRepository
    ) {
        this.customerOrdersReportRepository = customerOrdersReportRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerOrdersReportRowDTO> getReportsByCustomerId(
            Integer customerId,
            int page,
            int size
    ) {
        if (customerId == null || customerId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid customer ID"
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return customerOrdersReportRepository
                .findByCustomerId(customerId, pageable)
                .map(CustomerOrdersReportMapper::toDTO);
    }
}
