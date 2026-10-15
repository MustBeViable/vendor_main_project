package com.api_vendor.vendor_main.temporal.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductsHistoryDTO(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Integer categoryId,
        Integer supplierId,
        LocalDateTime versionStart,
        LocalDateTime versionEnd
) {}
