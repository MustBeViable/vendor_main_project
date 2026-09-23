package com.api_vendor.vendor_main.orders;

import com.api_vendor.vendor_main.customer_addresses.CustomerAddresses;
import com.api_vendor.vendor_main.customers.Customers;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @Column(
            name = "order_date",
            insertable = false,
            updatable = false
    )
    private LocalDateTime orderDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private CustomerAddresses shippingAddressId;

    @Column(name = "status")
    private String status;

    public Orders() {}
    public Orders(Customers customer, CustomerAddresses shippingAddressId) {
        this.customer = customer;
        this.shippingAddressId = shippingAddressId;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public CustomerAddresses getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(CustomerAddresses shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getStatus() {
        if (status == null) {
            return "NEW";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }
}
