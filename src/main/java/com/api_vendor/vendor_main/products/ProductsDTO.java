package com.api_vendor.vendor_main.products;

import java.math.BigDecimal;

public class ProductsDTO {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private Integer categoryId;
    private Integer supplierId;

    public ProductsDTO() {}

    public ProductsDTO(
            Integer id,
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            Integer categoryId,
            Integer supplierId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
}