package com.api_vendor.vendor_main.products;

import com.api_vendor.vendor_main.productcategories.ProductCategories;
import com.api_vendor.vendor_main.productcategories.ProductCategoriesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class ProductsCascadeTest implements CommandLineRunner {

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("=== CASCADE + LAZY TESTI ALKAA ===");

        ProductCategories category = new ProductCategories();
        category.setName("Testikategoria");
        category.setDescription("Cascaden testausta varten");

        Products p1 = new Products();
        p1.setName("Testituote 1");
        p1.setPrice(new BigDecimal("10.00"));
        p1.setStockQuantity(5);
        p1.setCategory(category);

        Products p2 = new Products();
        p2.setName("Testituote 2");
        p2.setPrice(new BigDecimal("20.00"));
        p2.setStockQuantity(3);
        p2.setCategory(category);

        category.getProducts().add(p1);
        category.getProducts().add(p2);

        ProductCategories saved = productCategoriesRepository.save(category);
        System.out.println("Tallennettu kategoria ID: " + saved.getCategoryID());

        System.out.println("Tuotteita yhteensä: " + productsRepository.count());

        ProductCategories found = productCategoriesRepository
                .findById(saved.getCategoryID())
                .orElseThrow();

        System.out.println("Haettu kategoria: " + found.getName());
        System.out.println("Tuotteet:");
        found.getProducts().forEach(p ->
                System.out.println("  - " + p.getName() + " | " + p.getPrice())
        );

        System.out.println("=== CASCADE + LAZY TESTI VALMIS ===");
    }

    private final ProductCategoriesRepository productCategoriesRepository;
    private final ProductsRepository productsRepository;

    public ProductsCascadeTest(ProductCategoriesRepository productCategoriesRepository,
                               ProductsRepository productsRepository) {
        this.productCategoriesRepository = productCategoriesRepository;
        this.productsRepository = productsRepository;
    }
}