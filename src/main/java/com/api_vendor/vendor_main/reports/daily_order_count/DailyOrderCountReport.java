package com.api_vendor.vendor_main.reports.daily_order_count;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "daily_order_count")
public class DailyOrderCountReport {

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "order_count")
    private Integer orderCount;

    public DailyOrderCountReport() {}

    public DailyOrderCountReport(
            LocalDate date,
            Integer orderCount
    ) {
        this.date = date;
        this.orderCount = orderCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getOrderCount() {
        return orderCount;
    }
}
