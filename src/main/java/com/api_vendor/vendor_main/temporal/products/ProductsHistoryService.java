package com.api_vendor.vendor_main.temporal.products;

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
public class ProductsHistoryService {

    private final JdbcTemplate jdbcTemplate;

    public ProductsHistoryService(
            @Qualifier("dataSource") DataSource dataSource
    ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Kaikki versiot tai tietyn ajanhetken versio.
    public List<ProductsHistoryDTO> getHistory(
            Integer id,
            LocalDateTime asOf
    ) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid product ID"
            );
        }

        List<ProductsHistoryDTO> history;

        if (asOf == null) {

            // Kaikki tuoteversiot, myös nykyinen.
            String sql = """
                    SELECT
                        id,
                        name,
                        description,
                        price,
                        stock_quantity,
                        category_id,
                        supplier_id,
                        ROW_START AS version_start,
                        ROW_END AS version_end
                    FROM products FOR SYSTEM_TIME ALL
                    WHERE id = ?
                    ORDER BY version_start DESC, version_end DESC
                    """;

            history = jdbcTemplate.query(
                    sql,
                    this::mapRow,
                    id
            );

        } else {

            // Tuotteen tila annettuna ajankohtana.
            String sql = """
                    SELECT
                        id,
                        name,
                        description,
                        price,
                        stock_quantity,
                        category_id,
                        supplier_id,
                        ROW_START AS version_start,
                        ROW_END AS version_end
                    FROM products FOR SYSTEM_TIME AS OF ?
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
                    "No product version found"
            );
        }

        return history;
    }

    private ProductsHistoryDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new ProductsHistoryDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock_quantity"),
                rs.getObject(
                        "category_id",
                        Integer.class
                ),
                rs.getObject(
                        "supplier_id",
                        Integer.class
                ),
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
