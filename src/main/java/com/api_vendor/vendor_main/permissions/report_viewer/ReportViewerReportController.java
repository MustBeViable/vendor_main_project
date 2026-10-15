
package com.api_vendor.vendor_main.permissions.report_viewer;

import com.api_vendor.vendor_main.reports.daily_order_count.DailyOrderCountReportRowDTO;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports/report-viewer")
public class ReportViewerReportController {

    private final ReportViewerReportService reportViewerReportService;

    public ReportViewerReportController(
            ReportViewerReportService reportViewerReportService
    ) {
        this.reportViewerReportService = reportViewerReportService;
    }

    @GetMapping("/daily-order-count")
    public Page<DailyOrderCountReportRowDTO> getDailyOrderCount(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return reportViewerReportService.getDailyOrderCount(
                from,
                to,
                page,
                size
        );
    }
}
