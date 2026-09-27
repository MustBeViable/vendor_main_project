package com.api_vendor.vendor_main.orderitems;

import java.math.BigDecimal;

public class OrderItemsDTO {

    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItemsDTO() {
    }

    public OrderItemsDTO(
            Integer orderId,
            Integer productId,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}