package com.api_vendor.vendor_main.productcategories;

public class ProductCategoriesMapper {

    private ProductCategoriesMapper() {}

    public static ProductCategories toEntity(ProductCategoriesDTO dto) {
        ProductCategories category = new ProductCategories();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    public static ProductCategoriesDTO toDTO(ProductCategories category) {
        return new ProductCategoriesDTO(
                category.getCategoryID(),
                category.getName(),
                category.getDescription()
        );
    }
}