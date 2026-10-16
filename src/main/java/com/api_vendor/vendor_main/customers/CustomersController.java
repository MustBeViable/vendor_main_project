package com.api_vendor.vendor_main.customers;

import com.api_vendor.vendor_main.orders.OrdersDTO;
import com.api_vendor.vendor_main.orders.OrdersService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersService customersService;
    private final OrdersService ordersService;

    public CustomersController(
            CustomersService customersService,
            OrdersService ordersService
    ) {
        this.customersService = customersService;
        this.ordersService = ordersService;
    }

    @GetMapping
    public Page<CustomersDTO> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return customersService.getAll(name, page, size);
    }

    @GetMapping("/{id}")
    public CustomersDTO getById(@PathVariable Integer id) {
        return customersService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomersDTO create(@RequestBody CustomersDTO dto) {
        return customersService.create(dto);
    }

    @PutMapping("/{id}")
    public CustomersDTO update(
            @PathVariable Integer id,
            @RequestBody CustomersDTO dto
    ) {
        return customersService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        customersService.delete(id);
    }

    @GetMapping("/{id}/orders")
    public Page<OrdersDTO> getCustomerOrders(
            @PathVariable Integer id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        customersService.getById(id);
        return ordersService.getAll(id, status, page, size);
    }
}