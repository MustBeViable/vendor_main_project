package com.api_vendor.vendor_main.temporal.orders;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdersHistoryService {

    private final JdbcTemplate jdbcTemplate;

    public OrdersHistoryService(
            @Qualifier("dataSource") DataSource dataSource
    ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Palauttaa kaikki versiot tai tietyn ajanhetken version.
    public List<OrdersHistoryDTO> getHistory(
            Integer id,
            LocalDateTime asOf
    ) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order ID"
            );
        }

        List<OrdersHistoryDTO> history;

        if (asOf == null) {

            // Kaikki historialliset ja nykyinen versio.
            String sql = """
                    SELECT
                        id,
                        customer_id,
                        order_date,
                        delivery_date,
                        shipping_address_id,
                        status,
                        ROW_START AS version_start,
                        ROW_END AS version_end
                    FROM orders FOR SYSTEM_TIME ALL
                    WHERE id = ?
                    ORDER BY version_start DESC, version_end DESC
                    """;

            history = jdbcTemplate.query(
                    sql,
                    this::mapRow,
                    id
            );

        } else {

            // Tilauksen tila juuri annettuna ajankohtana.
            String sql = """
                    SELECT
                        id,
                        customer_id,
                        order_date,
                        delivery_date,
                        shipping_address_id,
                        status,
                        ROW_START AS version_start,
                        ROW_END AS version_end
                    FROM orders FOR SYSTEM_TIME AS OF ?
                    WHERE id = ?
                    """;

            history = jdbcTemplate.query(
                    sql,
                    this::mapRow,
                    Timestamp.valueOf(asOf),
                    id
            );
        }

        if (history.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No order version found"
            );
        }

        return history;
    }

    private OrdersHistoryDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new OrdersHistoryDTO(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                toLocalDateTime(
                        rs.getTimestamp("order_date")
                ),
                toLocalDateTime(
                        rs.getTimestamp("delivery_date")
                ),
                rs.getObject(
                        "shipping_address_id",
                        Integer.class
                ),
                rs.getString("status"),
                toLocalDateTime(
                        rs.getTimestamp("version_start")
                ),
                toLocalDateTime(
                        rs.getTimestamp("version_end")
                )
        );
    }

    private LocalDateTime toLocalDateTime(
            Timestamp timestamp
    ) {
        return timestamp == null
                ? null
                : timestamp.toLocalDateTime();
    }
}
