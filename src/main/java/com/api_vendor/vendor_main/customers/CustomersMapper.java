package com.api_vendor.vendor_main.customers;

public class CustomersMapper {

    private CustomersMapper() {}

    public static Customers toEntity(CustomersDTO dto) {
        return new Customers(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone()
        );
    }

    public static CustomersDTO toDTO(Customers customer) {
        return new CustomersDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}