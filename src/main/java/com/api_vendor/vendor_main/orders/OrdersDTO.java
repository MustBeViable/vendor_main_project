package com.api_vendor.vendor_main.orders;

import java.time.LocalDateTime;

public class OrdersDTO {

    private int id;
    private int customerId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private Integer shippingAddressId;
    private String status;

    public OrdersDTO() {}

    public OrdersDTO(
            int customerId,
            LocalDateTime deliveryDate,
            Integer shippingAddressId,
            String status
    ) {
        this.customerId = customerId;
        this.deliveryDate = deliveryDate;
        this.shippingAddressId = shippingAddressId;
        this.status = status;
    }

    public OrdersDTO(
            int id,
            int customerId,
            LocalDateTime orderDate,
            LocalDateTime deliveryDate,
            Integer shippingAddressId,
            String status
    ) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.shippingAddressId = shippingAddressId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public Integer getShippingAddressId() {
        return shippingAddressId;
    }

    public String getStatus() {
        return status;
    }
}