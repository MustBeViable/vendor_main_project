package com.api_vendor.vendor_main.customeraddresses;

import com.api_vendor.vendor_main.customers.Customers;

public class CustomerAddressesMapper {

    private CustomerAddressesMapper() {}

    public static CustomerAddresses toEntity(
            CustomerAddressesDTO dto,
            Customers customer
    ) {
        return new CustomerAddresses(
                customer,
                dto.streetAddress(),
                dto.postalCode(),
                dto.city(),
                dto.country()
        );
    }

    public static CustomerAddressesDTO toDTO(
            CustomerAddresses address
    ) {
        return new CustomerAddressesDTO(
                address.getId(),
                address.getCustomer().getId(),
                address.getStreetAddress(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }

    public static void updateEntity(
            CustomerAddresses address,
            CustomerAddressesDTO dto,
            Customers customer
    ) {
        address.setCustomer(customer);
        address.setStreetAddress(dto.streetAddress());
        address.setPostalCode(dto.postalCode());
        address.setCity(dto.city());
        address.setCountry(dto.country());
    }
}
