
package com.api_vendor.vendor_main.reports.product_categories;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/product-categories")
public class ProductCategoriesReportController {

    private final ProductCategoriesReportService productCategoriesReportService;

    public ProductCategoriesReportController(
            ProductCategoriesReportService productCategoriesReportService
    ) {
        this.productCategoriesReportService = productCategoriesReportService;
    }

    @GetMapping
    public Page<ProductCategoriesReportRowDTO> getByCategoryId(
            @RequestParam Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return productCategoriesReportService.getReportsByCategoryId(
                categoryId, page, size
        );
    }
}
