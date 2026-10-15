
package com.api_vendor.vendor_main.reports.product_categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoriesReportRepository
        extends JpaRepository<ProductCategoriesReport, Integer> {

    Page<ProductCategoriesReport> findByCategoryId(
            Integer categoryId,
            Pageable pageable
    );
}
