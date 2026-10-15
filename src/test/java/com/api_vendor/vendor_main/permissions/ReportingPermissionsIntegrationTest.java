
package com.api_vendor.vendor_main.permissions;

import com.api_vendor.vendor_main.config.ReportingDataSourceConfig;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
                ReportingDataSourceConfig.class,
                ReportingPermissionsIntegrationTest.TestConfig.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ReportingPermissionsIntegrationTest {

    @TestConfiguration
    @EnableConfigurationProperties(DataSourceProperties.class)
    static class TestConfig {
    }

    @Autowired
    @Qualifier("employeeJdbcTemplate")
    private JdbcTemplate employeeJdbcTemplate;

    @Autowired
    @Qualifier("reportViewerJdbcTemplate")
    private JdbcTemplate reportViewerJdbcTemplate;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${app.reporting.report-viewer.username}")
    private String reportViewerUsername;

    @Value("${app.reporting.report-viewer.password}")
    private String reportViewerPassword;

    // =========================================
    // EMPLOYEE
    // =========================================

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

        assertSelectDenied(
                employeeJdbcTemplate,
                "SELECT * FROM orders LIMIT 1"
        );
    }

    // =========================================
    // REPORT VIEWER
    // =========================================

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

        assertSelectDenied(
                reportViewerJdbcTemplate,
                "SELECT * FROM orders LIMIT 1"
        );
    }

    @Test
    void reportViewerCannotUpdateDailyOrderCount()
            throws SQLException {

        // Tämä testi tarkistaa nimenomaan MariaDB:n
        // UPDATE-oikeuden, ei JDBC:n readOnly-asetusta.

        String sql = """
                UPDATE daily_order_count
                SET order_count = order_count
                WHERE 1 = 0
                """;

        try (
                Connection connection =
                        DriverManager.getConnection(
                                databaseUrl,
                                reportViewerUsername,
                                reportViewerPassword
                        );

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            SQLException exception = assertThrows(
                    SQLException.class,
                    statement::executeUpdate
            );

            assertEquals(
                    1142,
                    exception.getErrorCode()
            );
        }
    }

    // =========================================
    // HELPER
    // =========================================

    private void assertSelectDenied(
            JdbcTemplate jdbcTemplate,
            String sql
    ) {

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> jdbcTemplate.queryForList(sql)
        );

        Throwable cause = exception.getMostSpecificCause();

        assertInstanceOf(SQLException.class, cause);

        SQLException sqlException = (SQLException) cause;

        assertEquals(
                1142,
                sqlException.getErrorCode()
        );
    }
}
