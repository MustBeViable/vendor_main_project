package com.api_vendor.vendor_main.productcategories;

import org.springframework.stereotype.Service;

@Service
public class ProductCategoriesService {

    private final ProductCategoriesRepository productCategoriesRepository;

    public ProductCategoriesService(ProductCategoriesRepository productCategoriesRepository) {
        this.productCategoriesRepository = productCategoriesRepository;
    }

    public ProductCategories save(ProductCategories category) {
        if (category.getProducts() != null) {
            category.getProducts().forEach(p -> p.setCategory(category));
        }
        return productCategoriesRepository.save(category);
    }
}