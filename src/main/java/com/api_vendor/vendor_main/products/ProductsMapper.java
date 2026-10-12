package com.api_vendor.vendor_main.products;

public class ProductsMapper {

    private ProductsMapper() {}

    public static Products toEntity(ProductsDTO dto) {
        Products product = new Products();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setSupplierId(dto.getSupplierId());
        return product;
    }

    public static ProductsDTO toDTO(Products product) {
        Integer categoryId = null;
        if (product.getCategory() != null) {
            categoryId = product.getCategory().getCategoryID();
        }

        return new ProductsDTO(
                product.getProductID(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                categoryId,
                product.getSupplierId()
        );
    }
}