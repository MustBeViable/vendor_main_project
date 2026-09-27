package com.api_vendor.vendor_main.orderitems;

import com.api_vendor.vendor_main.orders.Orders;
import com.api_vendor.vendor_main.orders.OrdersRepository;
import com.api_vendor.vendor_main.products.Products;
import com.api_vendor.vendor_main.products.ProductsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderitems")
public class OrderItemsController {

    private final OrderItemsRepository orderItemsRepository;
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;

    public OrderItemsController(
            OrderItemsRepository orderItemsRepository,
            OrdersRepository ordersRepository,
            ProductsRepository productsRepository
    ) {
        this.orderItemsRepository = orderItemsRepository;
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
    }

    @GetMapping
    public List<OrderItemsDTO> getAllOrderItems() {
        return orderItemsRepository.findAll()
                .stream()
                .map(OrderItemsMapper::toDTO)
                .toList();
    }

    @GetMapping("/{orderId}/{productId}")
    public OrderItemsDTO getOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer productId
    ) {

        OrderItemId id = new OrderItemId(orderId, productId);

        OrderItems orderItem = orderItemsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        return OrderItemsMapper.toDTO(orderItem);
    }

    @PostMapping
    public OrderItemsDTO createOrderItem(
            @RequestBody OrderItemsDTO dto
    ) {

        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Products product = productsRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItems orderItem =
                OrderItemsMapper.toEntity(dto, order, product);

        OrderItems saved =
                orderItemsRepository.save(orderItem);

        return OrderItemsMapper.toDTO(saved);
    }

    @PutMapping("/{orderId}/{productId}")
    public OrderItemsDTO updateOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer productId,
            @RequestBody OrderItemsDTO dto
    ) {

        OrderItemId id = new OrderItemId(orderId, productId);

        OrderItems existing = orderItemsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        existing.setQuantity(dto.getQuantity());
        existing.setUnitPrice(dto.getUnitPrice());

        OrderItems saved =
                orderItemsRepository.save(existing);

        return OrderItemsMapper.toDTO(saved);
    }

    @DeleteMapping("/{orderId}/{productId}")
    public void deleteOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer productId
    ) {

        OrderItemId id = new OrderItemId(orderId, productId);

        if (!orderItemsRepository.existsById(id)) {
            throw new RuntimeException("Order item not found");
        }

        orderItemsRepository.deleteById(id);
    }
}