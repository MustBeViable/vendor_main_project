package com.api_vendor.vendor_main.customers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
