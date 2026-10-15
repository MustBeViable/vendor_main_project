package com.api_vendor.vendor_main.suppliers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SuppliersRepository
        extends JpaRepository<Suppliers, Integer> {

    Page<Suppliers> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT COUNT(*)
                FROM products
                WHERE supplier_id = :supplierId
                """,
            nativeQuery = true
    )
    long countProductsBySupplierId(
            @Param("supplierId") Integer supplierId
    );

    @Query(
            value = """
                SELECT COUNT(*)
                FROM supplieraddresses
                WHERE supplier_id = :supplierId
                """,
            nativeQuery = true
    )
    long countAddressesBySupplierId(
            @Param("supplierId") Integer supplierId
    );
}
