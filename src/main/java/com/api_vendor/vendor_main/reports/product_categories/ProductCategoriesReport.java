
package com.api_vendor.vendor_main.reports.product_categories;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "product_categories")
public class ProductCategoriesReport {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    public ProductCategoriesReport() {}

    public ProductCategoriesReport(
            Integer productId,
            String productName,
            Integer categoryId,
            String categoryName,
            BigDecimal price,
            Integer stockQuantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
}
