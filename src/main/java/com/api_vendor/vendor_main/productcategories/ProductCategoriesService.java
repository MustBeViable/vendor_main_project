package com.api_vendor.vendor_main.productcategories;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductCategoriesService {

    private final ProductCategoriesRepository productCategoriesRepository;

    public ProductCategoriesService(ProductCategoriesRepository productCategoriesRepository) {
        this.productCategoriesRepository = productCategoriesRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductCategories> findAll() {
        return productCategoriesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductCategories findById(int id) {
        return productCategoriesRepository.findById(id).orElseThrow();
    }

    @Transactional
    public ProductCategories save(ProductCategories category) {
        if (category.getProducts() != null) {
            category.getProducts().forEach(p -> p.setCategory(category));
        }
        return productCategoriesRepository.save(category);
    }

    @Transactional
    public ProductCategories update(int id, ProductCategoriesDTO dto) {
        ProductCategories category = productCategoriesRepository.findById(id).orElseThrow();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    @Transactional
    public void delete(int id) {
        productCategoriesRepository.deleteById(id);
    }
}