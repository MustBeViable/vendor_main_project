package com.api_vendor.vendor_main.customers;

import com.api_vendor.vendor_main.orders.Orders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersRepository customersRepository;

    public CustomersController(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    @PostMapping
    public Customers createCustomer(@RequestBody CustomersDTO customersDTO) {

        Customers customers = CustomersMapper.toEntity(customersDTO);

        return customersRepository.save(customers);
    }

    @GetMapping("/{id}/orders")
    public List<Orders> getCustomerOrders(@PathVariable int id) {

        Customers customer = customersRepository
                .findById(id)
                .orElseThrow();

        return customer.getOrders();
    }
}
