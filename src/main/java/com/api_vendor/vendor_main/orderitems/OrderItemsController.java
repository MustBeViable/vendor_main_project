package com.api_vendor.vendor_main.orderitems;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderitems")
public class OrderItemsController {

    private final OrderItemsService service;

    public OrderItemsController(OrderItemsService service) {
        this.service = service;
    }

    @GetMapping
    public Page<OrderItemsDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getAll(page, size);
    }

    @GetMapping("/{orderId}/{productId}")
    public OrderItemsDTO getById(
            @PathVariable Integer orderId,
            @PathVariable Integer productId
    ) {
        return service.getById(orderId, productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemsDTO create(@RequestBody OrderItemsDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{orderId}/{productId}")
    public OrderItemsDTO update(
            @PathVariable Integer orderId,
            @PathVariable Integer productId,
            @RequestBody OrderItemsDTO dto
    ) {
        return service.update(orderId, productId, dto);
    }

    @DeleteMapping("/{orderId}/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Integer orderId,
            @PathVariable Integer productId
    ) {
        service.delete(orderId, productId);
    }
}
