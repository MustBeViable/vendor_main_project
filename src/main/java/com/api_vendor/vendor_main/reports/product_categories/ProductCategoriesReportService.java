
package com.api_vendor.vendor_main.reports.product_categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductCategoriesReportService {

    private final ProductCategoriesReportRepository productCategoriesReportRepository;

    public ProductCategoriesReportService(
            ProductCategoriesReportRepository productCategoriesReportRepository
    ) {
        this.productCategoriesReportRepository = productCategoriesReportRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductCategoriesReportRowDTO> getReportsByCategoryId(
            Integer categoryId,
            int page,
            int size
    ) {
        if (categoryId == null || categoryId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid category ID"
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return productCategoriesReportRepository
                .findByCategoryId(categoryId, pageable)
                .map(ProductCategoriesReportMapper::toDTO);
    }
}
