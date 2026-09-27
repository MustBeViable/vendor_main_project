package com.api_vendor.vendor_main.orderitems;

import com.api_vendor.vendor_main.orders.Orders;
import com.api_vendor.vendor_main.products.Products;

public class OrderItemsMapper {

    public static OrderItems toEntity(
            OrderItemsDTO dto,
            Orders order,
            Products product
    ) {
        OrderItems orderItem = new OrderItems();

        OrderItemId id = new OrderItemId(
                dto.getOrderId(),
                dto.getProductId()
        );

        orderItem.setId(id);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setUnitPrice(dto.getUnitPrice());

        return orderItem;
    }

    public static OrderItemsDTO toDTO(OrderItems orderItem) {
        return new OrderItemsDTO(
                orderItem.getId().getOrderId(),
                orderItem.getId().getProductId(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice()
        );
    }
}