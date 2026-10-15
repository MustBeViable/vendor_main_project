package com.api_vendor.vendor_main.customeraddresses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerAddressesRepository
        extends JpaRepository<CustomerAddresses, Integer> {

    Page<CustomerAddresses> findByCustomer_Id(
            Integer customerId,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT COUNT(*)
                FROM orders
                WHERE shipping_address_id = :addressId
                """,
            nativeQuery = true
    )
    long countOrdersUsingAddress(
            @Param("addressId") Integer addressId
    );
}
