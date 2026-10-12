
package com.api_vendor.vendor_main.supplieraddresses;

import com.api_vendor.vendor_main.suppliers.Suppliers;

public class SupplierAddressesMapper {

    private SupplierAddressesMapper() {}

    public static SupplierAddresses toEntity(
            SupplierAddressesDTO dto,
            Suppliers supplier
    ) {
        return new SupplierAddresses(
                supplier,
                dto.streetAddress(),
                dto.postalCode(),
                dto.city(),
                dto.country()
        );
    }

    public static SupplierAddressesDTO toDTO(
            SupplierAddresses address
    ) {
        return new SupplierAddressesDTO(
                address.getId(),
                address.getSupplier().getSuppliersID(),
                address.getStreetAddress(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }

    public static void updateEntity(
            SupplierAddresses address,
            SupplierAddressesDTO dto,
            Suppliers supplier
    ) {
        address.setSupplier(supplier);
        address.setStreetAddress(dto.streetAddress());
        address.setPostalCode(dto.postalCode());
        address.setCity(dto.city());
        address.setCountry(dto.country());
    }
}
