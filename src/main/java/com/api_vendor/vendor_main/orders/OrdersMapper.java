package com.api_vendor.vendor_main.orders;

import com.api_vendor.vendor_main.customer_addresses.CustomerAddresses;
import com.api_vendor.vendor_main.customers.Customers;

public class OrdersMapper {

    private OrdersMapper() {}

    public static Orders toEntity(
            OrdersDTO dto,
            Customers customer,
            CustomerAddresses shippingAddress
    ) {
        Orders order = new Orders(
                customer,
                shippingAddress
        );

        order.setDeliveryDate(dto.getDeliveryDate());
        order.setStatus(dto.getStatus());

        return order;
    }

    public static OrdersDTO toDTO(Orders order) {

        Integer shippingAddressId = null;

        if (order.getShippingAddressId() != null) {
            shippingAddressId = order.getShippingAddressId().getId();
        }

        return new OrdersDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderDate(),
                order.getDeliveryDate(),
                shippingAddressId,
                order.getStatus()
        );
    }
}