
package com.api_vendor.vendor_main.permissions;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ReportingPermissionsIntegrationTest {

    @Autowired
    @Qualifier("employeeJdbcTemplate")
    private JdbcTemplate employeeJdbcTemplate;

    @Autowired
    @Qualifier("reportViewerJdbcTemplate")
    private JdbcTemplate reportViewerJdbcTemplate;

    @Test
    void employeeUsesCorrectDatabaseUser() {
        String user = employeeJdbcTemplate.queryForObject(
                "SELECT CURRENT_USER()",
                String.class
        );

        assertNotNull(user);
        assertTrue(user.startsWith("employee@"));
    }

    @Test
    void employeeCanReadCustomerOrders() {
        assertDoesNotThrow(() ->
                employeeJdbcTemplate.queryForList(
                        "SELECT * FROM customer_orders LIMIT 1"
                )
        );
    }

    @Test
    void employeeCannotReadOrdersTable() {
        assertThrows(
                DataAccessException.class,
                () -> employeeJdbcTemplate.queryForList(
                        "SELECT * FROM orders LIMIT 1"
                )
        );
    }

    @Test
    void reportViewerUsesCorrectDatabaseUser() {
        String user = reportViewerJdbcTemplate.queryForObject(
                "SELECT CURRENT_USER()",
                String.class
        );

        assertNotNull(user);
        assertTrue(user.startsWith("report_viewer@"));
    }

    @Test
    void reportViewerCanReadDailyOrderCount() {
        assertDoesNotThrow(() ->
                reportViewerJdbcTemplate.queryForList(
                        "SELECT * FROM daily_order_count LIMIT 1"
                )
        );
    }

    @Test
    void reportViewerCannotReadOrdersTable() {
        assertThrows(
                DataAccessException.class,
                () -> reportViewerJdbcTemplate.queryForList(
                        "SELECT * FROM orders LIMIT 1"
                )
        );
    }

    @Test
    void reportViewerCannotUpdateDailyOrderCount() {
        assertThrows(
                DataAccessException.class,
                () -> reportViewerJdbcTemplate.update(
                        """
                        UPDATE daily_order_count
                        SET order_count = order_count
                        WHERE 1 = 0
                        """
                )
        );
    }
}
