package com.api_vendor.vendor_main.productcategories;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoriesTest implements CommandLineRunner {

    @Override
    public void run(String... args) {

        System.out.println("=== PRODUCT CATEGORIES TESTI ALKAA ===");

        System.out.println(productCategoriesRepository.findAll());

        ProductCategories category = new ProductCategories();
        category.setName("Testi kategoria");
        category.setDescription("Testikuvaus");

        ProductCategories saved = productCategoriesRepository.save(category);

        System.out.println("Tallennettu ID: " + saved.getCategoryID());

        System.out.println(
                productCategoriesRepository.findById(saved.getCategoryID())
        );

        System.out.println("=== PRODUCT CATEGORIES TESTI VALMIS ===");
    }

    private final ProductCategoriesRepository productCategoriesRepository;

    public ProductCategoriesTest(ProductCategoriesRepository productCategoriesRepository) {
        this.productCategoriesRepository = productCategoriesRepository;
    }
}