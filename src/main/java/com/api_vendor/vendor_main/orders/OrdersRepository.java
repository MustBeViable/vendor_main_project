
package com.api_vendor.vendor_main.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository
        extends JpaRepository<Orders, Integer> {

    @Override
    @EntityGraph(attributePaths = {"customer", "shippingAddressId"})
    Page<Orders> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "shippingAddressId"})
    Page<Orders> findByStatus(
            String status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"customer", "shippingAddressId"})
    Page<Orders> findByCustomer_Id(
            Integer customerId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"customer", "shippingAddressId"})
    Page<Orders> findByCustomer_IdAndStatus(
            Integer customerId,
            String status,
            Pageable pageable
    );
}
