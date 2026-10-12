
package com.api_vendor.vendor_main.orders;

import com.api_vendor.vendor_main.orderitems.OrderItemsDTO;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping
    public Page<OrdersDTO> getAll(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ordersService.getAll(
                customerId, status, page, size
        );
    }

    @GetMapping("/{id}")
    public OrdersDTO getById(@PathVariable Integer id) {
        return ordersService.getById(id);
    }

    @GetMapping("/{id}/items")
    public List<OrderItemsDTO> getItems(@PathVariable Integer id) {
        return ordersService.getItems(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdersDTO create(@RequestBody OrdersDTO dto) {
        return ordersService.create(dto);
    }

    @PutMapping("/{id}")
    public OrdersDTO update(
            @PathVariable Integer id,
            @RequestBody OrdersDTO dto
    ) {
        return ordersService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public OrdersDTO updateStatus(
            @PathVariable Integer id,
            @RequestBody OrderStatusDTO dto
    ) {
        return ordersService.updateStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        ordersService.delete(id);
    }
}
