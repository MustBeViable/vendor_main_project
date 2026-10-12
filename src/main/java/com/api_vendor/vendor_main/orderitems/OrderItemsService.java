package com.api_vendor.vendor_main.orderitems;

import com.api_vendor.vendor_main.orders.Orders;
import com.api_vendor.vendor_main.orders.OrdersRepository;
import com.api_vendor.vendor_main.products.Products;
import com.api_vendor.vendor_main.products.ProductsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class OrderItemsService {

    private final OrderItemsRepository itemsRepository;
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;

    public OrderItemsService(
            OrderItemsRepository itemsRepository,
            OrdersRepository ordersRepository,
            ProductsRepository productsRepository
    ) {
        this.itemsRepository = itemsRepository;
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderItemsDTO> getAll(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0 and size 1..100");
        }
        return itemsRepository.findAll(PageRequest.of(page, size))
                .map(OrderItemsMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderItemsDTO getById(Integer orderId, Integer productId) {
        return OrderItemsMapper.toDTO(findItem(orderId, productId));
    }

    @Transactional
    public OrderItemsDTO create(OrderItemsDTO dto) {
        validate(dto);
        if (dto.getOrderId() == null || dto.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId and productId are required");
        }

        OrderItemId id = new OrderItemId(dto.getOrderId(), dto.getProductId());
        if (itemsRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order item already exists");
        }

        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot add items to cancelled order");
        }

        Products product = productsRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        OrderItems item = OrderItemsMapper.toEntity(dto, order, product);
        // Stock must be decremented by the database trigger; do not decrement it again in Java.
        return OrderItemsMapper.toDTO(itemsRepository.saveAndFlush(item));
    }

    @Transactional
    public OrderItemsDTO update(Integer orderId, Integer productId, OrderItemsDTO dto) {
        validate(dto);
        OrderItems item = findItem(orderId, productId);

        if ("CANCELLED".equalsIgnoreCase(item.getOrder().getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot edit cancelled order");
        }
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());

        // UPDATE trigger is responsible for the stock difference.
        return OrderItemsMapper.toDTO(itemsRepository.saveAndFlush(item));
    }

    @Transactional
    public void delete(Integer orderId, Integer productId) {
        OrderItems item = findItem(orderId, productId);
        if ("CANCELLED".equalsIgnoreCase(item.getOrder().getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot edit cancelled order");
        }
        // DELETE trigger is responsible for restoring stock.
        itemsRepository.delete(item);
        itemsRepository.flush();
    }

    private OrderItems findItem(Integer orderId, Integer productId) {
        if (orderId == null || productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order and product IDs are required");
        }
        return itemsRepository.findById(new OrderItemId(orderId, productId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found"));
    }

    private void validate(OrderItemsDTO dto) {
        if (dto == null || dto.getQuantity() == null || dto.getQuantity() <= 0
                || dto.getUnitPrice() == null || dto.getUnitPrice().compareTo(BigDecimal.ZERO) < 0
                || dto.getUnitPrice().scale() > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "quantity must be positive and unitPrice a nonnegative amount with at most two decimals");
        }
    }
}
