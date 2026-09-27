package com.api_vendor.vendor_main.orderitems;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository<OrderItems, OrderItemId> {
}