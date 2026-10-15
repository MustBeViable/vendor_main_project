package com.api_vendor.vendor_main.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Integer> {

    List<Products> findAllByCategory_CategoryID(Integer categoryId);

    List<Products> findByNameStartingWith(String name);

    List<Products> findByStockQuantityLessThanEqual(int threshold);

    @Procedure(procedureName = "stock_increase")
    void stockIncrease(
            @Param("p_product_id") int productId,
            @Param("p_quantity") int quantity
    );

    @Procedure(procedureName = "stock_decrease")
    void stockDecrease(
            @Param("p_product_id") int productId,
            @Param("p_quantity") int quantity
    );
}