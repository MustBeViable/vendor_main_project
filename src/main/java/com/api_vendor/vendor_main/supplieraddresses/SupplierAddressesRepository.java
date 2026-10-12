
package com.api_vendor.vendor_main.supplieraddresses;

import com.api_vendor.vendor_main.suppliers.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierAddressesRepository
        extends JpaRepository<SupplierAddresses, Integer> {

    List<SupplierAddresses> findAllBySupplier(Suppliers supplier);
}
