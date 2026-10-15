package com.api_vendor.vendor_main.reports.customer_orders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "customer_orders")
public class CustomerOrdersReport {

    @Id
    @Column(name = "report_row_id")
    private String reportRowId;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "line_total")
    private BigDecimal lineTotal;

    public CustomerOrdersReport() {}

    public CustomerOrdersReport(
            String reportRowId, Integer orderId, Integer customerId, String firstName,
            String lastName, LocalDateTime orderDate, String orderStatus, Integer productId,
            Integer quantity, String productName, BigDecimal unitPrice, BigDecimal lineTotal
    ) {
        this.reportRowId = reportRowId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public String getReportRowId() {
        return reportRowId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}