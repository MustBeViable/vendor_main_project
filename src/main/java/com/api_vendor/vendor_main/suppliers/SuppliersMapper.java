package com.api_vendor.vendor_main.suppliers;

public class SuppliersMapper {

    private SuppliersMapper() {}

    public static Suppliers toEntity(SuppliersDTO dto) {
        return new Suppliers(
                dto.name(),
                dto.phone(),
                dto.contactName(),
                dto.email()
        );
    }

    public static SuppliersDTO toDTO(Suppliers supplier) {
        return new SuppliersDTO(
                supplier.getSuppliersID(),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getPhone(),
                supplier.getEmail()
        );
    }

    public static void updateEntity(
            Suppliers supplier,
            SuppliersDTO dto
    ) {
        supplier.setName(dto.name());
        supplier.setContactName(dto.contactName());
        supplier.setPhone(dto.phone());
        supplier.setEmail(dto.email());
    }
}
