package com.api_vendor.vendor_main.orders;

import com.api_vendor.vendor_main.customer_addresses.CustomerAddresses;
import com.api_vendor.vendor_main.customer_addresses.CustomerAddressesRepository;
import com.api_vendor.vendor_main.customers.Customers;
import com.api_vendor.vendor_main.customers.CustomersRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersRepository ordersRepository;
    private final CustomersRepository customersRepository;
    private final CustomerAddressesRepository customerAddressesRepository;

    public OrdersController(
            OrdersRepository ordersRepository,
            CustomersRepository customersRepository,
            CustomerAddressesRepository customerAddressesRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.customersRepository = customersRepository;
        this.customerAddressesRepository = customerAddressesRepository;
    }

    @PostMapping
    public OrdersDTO createOrder(@RequestBody OrdersDTO ordersDTO) {

        Customers customer = customersRepository
                .findById(ordersDTO.getCustomerId())
                .orElseThrow();

        CustomerAddresses shippingAddress = null;

        if (ordersDTO.getShippingAddressId() != null) {
            shippingAddress = customerAddressesRepository
                    .findById(ordersDTO.getShippingAddressId())
                    .orElseThrow();
        }

        Orders order = OrdersMapper.toEntity(
                ordersDTO,
                customer,
                shippingAddress
        );

        Orders savedOrder = ordersRepository.save(order);

        return OrdersMapper.toDTO(savedOrder);
    }
}