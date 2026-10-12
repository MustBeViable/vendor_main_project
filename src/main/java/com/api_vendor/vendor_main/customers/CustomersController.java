package com.api_vendor.vendor_main.customers;

import com.api_vendor.vendor_main.orders.OrdersDTO;
import com.api_vendor.vendor_main.orders.OrdersService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersRepository customersRepository;
    private final OrdersService ordersService;

    public CustomersController(CustomersRepository customersRepository, OrdersService ordersService) {
        this.customersRepository = customersRepository;
        this.ordersService = ordersService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomersDTO createCustomer(@RequestBody CustomersDTO dto) {
        if (dto == null || dto.getFirstName() == null || dto.getFirstName().isBlank()
                || dto.getLastName() == null || dto.getLastName().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "First name, last name and email are required");
        }
        return CustomersMapper.toDTO(customersRepository.save(CustomersMapper.toEntity(dto)));
    }

    @GetMapping("/{id}/orders")
    public Page<OrdersDTO> getCustomerOrders(
            @PathVariable Integer id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (!customersRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Customer not found");
        }
        return ordersService.getAll(id, status, page, size);
    }
}
