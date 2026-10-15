package com.api_vendor.vendor_main.orders;

import com.api_vendor.vendor_main.customers.Customers;
import com.api_vendor.vendor_main.customers.CustomersRepository;
import com.api_vendor.vendor_main.customeraddresses.CustomerAddresses;
import com.api_vendor.vendor_main.customeraddresses.CustomerAddressesRepository;
import com.api_vendor.vendor_main.orderitems.OrderItemsDTO;
import com.api_vendor.vendor_main.orderitems.OrderItemsMapper;
import com.api_vendor.vendor_main.orderitems.OrderItemsRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final CustomersRepository customersRepository;
    private final CustomerAddressesRepository addressesRepository;
    private final OrderItemsRepository orderItemsRepository;

    public OrdersService(
            OrdersRepository ordersRepository,
            CustomersRepository customersRepository,
            CustomerAddressesRepository addressesRepository,
            OrderItemsRepository orderItemsRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.customersRepository = customersRepository;
        this.addressesRepository = addressesRepository;
        this.orderItemsRepository = orderItemsRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrdersDTO> getAll(
            Integer customerId,
            String status,
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        PageRequest pageable = PageRequest.of(page, size);

        String filterStatus = status == null
                ? null
                : normalizeStatus(status);

        Page<Orders> orders;

        if (customerId != null && filterStatus != null) {
            orders = ordersRepository.findByCustomer_IdAndStatusOrderByOrderDateDesc(
                    customerId, filterStatus, pageable
            );
        } else if (customerId != null) {
            orders = ordersRepository.findByCustomer_IdOrderByOrderDateDesc(
                    customerId, pageable
            );
        } else if (filterStatus != null) {
            orders = ordersRepository.findByStatusOrderByOrderDateDesc(
                    filterStatus, pageable
            );
        } else {
            orders = ordersRepository.findAllByOrderByOrderDateDesc(pageable);
        }

        return orders.map(OrdersMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrdersDTO getById(Integer id) {
        return OrdersMapper.toDTO(findOrder(id));
    }

    @Transactional(readOnly = true)
    public List<OrderItemsDTO> getItems(Integer id) {
        findOrder(id);

        return orderItemsRepository.findAllByOrder_Id(id)
                .stream()
                .map(OrderItemsMapper::toDTO)
                .toList();
    }

    @Transactional
    public OrdersDTO create(OrdersDTO dto) {

        Customers customer = findCustomer(dto.getCustomerId());

        CustomerAddresses address = findAddress(
                dto.getShippingAddressId(), customer
        );

        String status = dto.getStatus() == null
                ? "NEW"
                : validateNewStatus(dto.getStatus());

        Orders order = OrdersMapper.toEntity(
                dto, customer, address
        );

        order.setStatus(status);

        return OrdersMapper.toDTO(ordersRepository.save(order));
    }

    @Transactional
    public OrdersDTO update(Integer id, OrdersDTO dto) {

        Orders order = findOrder(id);

        Customers customer = findCustomer(dto.getCustomerId());

        CustomerAddresses address = findAddress(
                dto.getShippingAddressId(), customer
        );

        String status = validateNewStatus(dto.getStatus());
        checkTransition(order.getStatus(), status);

        OrdersMapper.updateEntity(
                order, dto, customer, address
        );

        order.setStatus(status);

        return OrdersMapper.toDTO(ordersRepository.save(order));
    }

    @Transactional
    public OrdersDTO updateStatus(
            Integer id,
            OrderStatusDTO dto
    ) {
        Orders order = findOrder(id);

        String newStatus = validateNewStatus(dto.status());
        checkTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        return OrdersMapper.toDTO(ordersRepository.save(order));
    }

    @Transactional
    public void delete(Integer id) {

        Orders order = findOrder(id);

        if (orderItemsRepository.existsByOrder_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Order contains items and cannot be deleted"
            );
        }

        ordersRepository.delete(order);
    }

    private Orders findOrder(Integer id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"
                ));
    }

    private Customers findCustomer(Integer id) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid customer ID is required"
            );
        }

        return customersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));
    }

    private CustomerAddresses findAddress(
            Integer addressId,
            Customers customer
    ) {
        if (addressId == null) {
            return null;
        }

        CustomerAddresses address = addressesRepository
                .findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shipping address not found"
                ));

        if (address.getCustomer().getId() != customer.getId()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Shipping address belongs to another customer"
            );
        }

        return address;
    }

    private String normalizeStatus(String status) {

        if (status == null || status.isBlank()
                || status.trim().length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order status"
            );
        }

        return status.trim().toUpperCase(Locale.ROOT);
    }

    private String validateNewStatus(String status) {

        String normalized = normalizeStatus(status);

        if ("CANCELLED".equals(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cancellation requires stock handling"
            );
        }

        return normalized;
    }

    private void checkTransition(
            String currentStatus,
            String newStatus
    ) {
        if ("CANCELLED".equalsIgnoreCase(currentStatus)
                && !"CANCELLED".equals(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cancelled order cannot be reopened"
            );
        }
    }
}