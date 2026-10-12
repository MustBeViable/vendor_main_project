
package com.api_vendor.vendor_main.orderitems;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemsRepository
        extends JpaRepository<OrderItems, OrderItemId> {

    List<OrderItems> findAllByOrder_Id(Integer orderId);

    boolean existsByOrder_Id(Integer orderId);
}
