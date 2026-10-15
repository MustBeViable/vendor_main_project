package com.api_vendor.vendor_main.customeraddresses;

public record CustomerAddressesDTO(
        Integer id,
        Integer customerId,
        String streetAddress,
        String postalCode,
        String city,
        String country
) {}