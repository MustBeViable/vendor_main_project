
package com.api_vendor.vendor_main.supplieraddresses;

public record SupplierAddressesDTO(
        Integer id,
        Integer supplierId,
        String streetAddress,
        String postalCode,
        String city,
        String country
) {}
