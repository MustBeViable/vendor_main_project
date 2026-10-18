package com.api_vendor.vendor_main.orders;

import com.api_vendor.vendor_main.orderitems.OrderItemId;
import com.api_vendor.vendor_main.orderitems.OrderItemsDTO;
import com.api_vendor.vendor_main.orderitems.OrderItemsRepository;
import com.api_vendor.vendor_main.orderitems.OrderItemsService;
import com.api_vendor.vendor_main.products.Products;
import com.api_vendor.vendor_main.products.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcurrentOrderTest {

    @Autowired
    private OrderItemsService orderItemsService;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    // Two orders at the same time should not oversell the product.
    @Test
    void twoConcurrentOrdersDoNotOversell() throws Exception {

        int productId = 1;
        int orderId = 1;

        // Clean up any old row and make sure the order is in NEW status.
        OrderItemId id = new OrderItemId(orderId, productId);
        if (orderItemsRepository.existsById(id)) {
            orderItemsRepository.deleteById(id);
            orderItemsRepository.flush();
        }
        Orders order = ordersRepository.findById(orderId).orElseThrow();
        order.setStatus("NEW");
        ordersRepository.save(order);

        // Only 1 pc in stock, only one order should succeed.
        Products product = productsRepository.findById(productId).orElseThrow();
        product.setStockQuantity(1);
        productsRepository.save(product);

        // One order attempt: order 1 pc.
        Runnable orderAttempt = () -> {
            try {
                OrderItemsDTO dto = new OrderItemsDTO();
                dto.setOrderId(orderId);
                dto.setProductId(productId);
                dto.setQuantity(1);
                dto.setUnitPrice(BigDecimal.valueOf(10.00));

                orderItemsService.create(dto);
            } catch (Exception e) {
                // The second thread is expected to fail.
            }
        };

        // Start both threads at the same time.
        Thread first = new Thread(orderAttempt);
        Thread second = new Thread(orderAttempt);

        first.start();
        second.start();

        first.join();
        second.join();

        // Stock must not go negative, it should be 0, not -1.
        Products after = productsRepository.findById(productId).orElseThrow();
        assertEquals(0, after.getStockQuantity());
    }
}