
package com.api_vendor.vendor_main.permissions.report_viewer;

import com.api_vendor.vendor_main.reports.daily_order_count.DailyOrderCountReportRowDTO;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportViewerReportService {

    private final JdbcTemplate jdbcTemplate;

    public ReportViewerReportService(
            @Qualifier("reportViewerJdbcTemplate")
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<DailyOrderCountReportRowDTO> getDailyOrderCount(
            LocalDate from,
            LocalDate to,
            int page,
            int size
    ) {

        if (from == null || to == null || from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date range"
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
                    date,
                    order_count
                FROM daily_order_count
                WHERE date BETWEEN ? AND ?
                ORDER BY date DESC
                LIMIT ? OFFSET ?
                """;

        List<DailyOrderCountReportRowDTO> reports =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new DailyOrderCountReportRowDTO(
                                        rs.getDate("date").toLocalDate(),
                                        rs.getInt("order_count")
                                ),
                        Date.valueOf(from),
                        Date.valueOf(to),
                        size,
                        pageable.getOffset()
                );

        Long total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM daily_order_count
                WHERE date BETWEEN ? AND ?
                """,
                Long.class,
                Date.valueOf(from),
                Date.valueOf(to)
        );

        return new PageImpl<>(
                reports,
                pageable,
                total == null ? 0L : total
        );
    }
}
