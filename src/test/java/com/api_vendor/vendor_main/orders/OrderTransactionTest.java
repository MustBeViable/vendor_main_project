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
class OrderTransactionTest {

    @Autowired
    private OrderItemsService orderItemsService;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    // Checks that a failed order item rolls back the whole transaction.
    @Test
    void insufficientStockDoesNotCreateOrderItem() {

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

        // Set stock to 5 so we know the starting value.
        Products product = productsRepository.findById(productId).orElseThrow();
        product.setStockQuantity(5);
        productsRepository.save(product);

        // Try to order 100 pcs, more than there is in stock.
        OrderItemsDTO dto = new OrderItemsDTO();
        dto.setOrderId(orderId);
        dto.setProductId(productId);
        dto.setQuantity(100);
        dto.setUnitPrice(BigDecimal.valueOf(10.00));

        // The trigger should raise "Insufficient stock".
        Exception ex = assertThrows(
                Exception.class,
                () -> orderItemsService.create(dto)
        );

        String message = ex.getMessage() != null ? ex.getMessage() : "";
        String causeMessage = ex.getCause() != null && ex.getCause().getMessage() != null
                ? ex.getCause().getMessage() : "";

        assertTrue(
                message.contains("Insufficient stock")
                        || causeMessage.contains("Insufficient stock"),
                "Expected 'Insufficient stock' error"
        );

        // No order item should exist, because the transaction was rolled back.
        assertFalse(
                orderItemsRepository.existsById(
                        new OrderItemId(orderId, productId)
                )
        );

        // Stock should still be 5.
        Products after = productsRepository.findById(productId).orElseThrow();
        assertEquals(5, after.getStockQuantity());
    }
}