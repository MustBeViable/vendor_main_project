
package com.api_vendor.vendor_main.reports.product_categories;

public class ProductCategoriesReportMapper {

    public static ProductCategoriesReportRowDTO toDTO(
            ProductCategoriesReport report
    ) {
        return new ProductCategoriesReportRowDTO(
                report.getProductId(),
                report.getProductName(),
                report.getCategoryId(),
                report.getCategoryName(),
                report.getPrice(),
                report.getStockQuantity()
        );
    }
}
