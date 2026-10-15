
package com.api_vendor.vendor_main.permissions.employee;

import com.api_vendor.vendor_main.reports.customer_orders.CustomerOrdersReportRowDTO;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeReportService {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeReportService(
            @Qualifier("employeeJdbcTemplate")
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<CustomerOrdersReportRowDTO> getCustomerOrders(
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

        PageRequest pageable = PageRequest.of(page, size);

        String sql = """
                SELECT
                    order_id,
                    customer_id,
                    first_name,
                    last_name,
                    order_date,
                    order_status,
                    product_id,
                    product_name,
                    quantity,
                    unit_price,
                    line_total
                FROM customer_orders
                WHERE customer_id = ?
                ORDER BY order_id DESC, product_id ASC
                LIMIT ? OFFSET ?
                """;

        List<CustomerOrdersReportRowDTO> reports =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new CustomerOrdersReportRowDTO(
                                        rs.getInt("order_id"),
                                        rs.getInt("customer_id"),
                                        rs.getString("first_name"),
                                        rs.getString("last_name"),
                                        rs.getTimestamp("order_date")
                                                .toLocalDateTime(),
                                        rs.getString("order_status"),
                                        rs.getObject(
                                                "product_id",
                                                Integer.class
                                        ),
                                        rs.getString("product_name"),
                                        rs.getObject(
                                                "quantity",
                                                Integer.class
                                        ),
                                        rs.getBigDecimal("unit_price"),
                                        rs.getBigDecimal("line_total")
                                ),
                        customerId,
                        size,
                        pageable.getOffset()
                );

        Long total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM customer_orders
                WHERE customer_id = ?
                """,
                Long.class,
                customerId
        );

        return new PageImpl<>(
                reports,
                pageable,
                total == null ? 0L : total
        );
    }
}
