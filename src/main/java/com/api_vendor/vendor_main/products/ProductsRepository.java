package com.api_vendor.vendor_main.products;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Integer> {

    List<Products> findAllByCategory_CategoryID(Integer categoryId);
}