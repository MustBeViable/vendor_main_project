package com.api_vendor.vendor_main.productcategories;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoriesController {

    private final ProductCategoriesService productCategoriesService;

    public ProductCategoriesController(ProductCategoriesService productCategoriesService) {
        this.productCategoriesService = productCategoriesService;
    }

    @PostMapping
    public ResponseEntity<ProductCategories> createCategory(@RequestBody ProductCategories category) {
        ProductCategories saved = productCategoriesService.save(category);
        return ResponseEntity.ok(saved);
    }
}