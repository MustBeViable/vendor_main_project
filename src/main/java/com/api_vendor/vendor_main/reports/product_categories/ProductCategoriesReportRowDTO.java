
package com.api_vendor.vendor_main.reports.product_categories;

import java.math.BigDecimal;

public record ProductCategoriesReportRowDTO(
        Integer productId,
        String productName,
        Integer categoryId,
        String categoryName,
        BigDecimal price,
        Integer stockQuantity
) {}
