package com.api_vendor.vendor_main.temporal.orders;

import java.time.LocalDateTime;

public record OrdersHistoryDTO(
        Integer id,
        Integer customerId,
        LocalDateTime orderDate,
        LocalDateTime deliveryDate,
        Integer shippingAddressId,
        String status,
        LocalDateTime versionStart,
        LocalDateTime versionEnd
) {}
