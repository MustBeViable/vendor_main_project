package com.api_vendor.vendor_main.customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomersService {

    private final CustomersRepository customersRepository;

    public CustomersService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomersDTO> getAll(String name, int page, int size) {
        validatePagination(page, size);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );

        Page<Customers> customers;

        if (name == null || name.isBlank()) {
            customers = customersRepository.findAll(pageable);
        } else {
            customers = customersRepository
                    .findByFirstNameContainingIgnoreCase(name.trim(), pageable);
        }

        return customers.map(CustomersMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CustomersDTO getById(Integer id) {
        return CustomersMapper.toDTO(findCustomer(id));
    }

    @Transactional
    public CustomersDTO create(CustomersDTO dto) {
        validate(dto);
        Customers saved = customersRepository.save(CustomersMapper.toEntity(dto));
        return CustomersMapper.toDTO(saved);
    }

    @Transactional
    public CustomersDTO update(Integer id, CustomersDTO dto) {
        validate(dto);

        Customers customer = findCustomer(id);
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());

        return CustomersMapper.toDTO(customer);
    }

    @Transactional
    public void delete(Integer id) {
        Customers customer = findCustomer(id);

        if (!customer.getOrders().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Customer has orders and cannot be deleted"
            );
        }

        customersRepository.delete(customer);
    }

    private Customers findCustomer(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid customer ID"
            );
        }
        return customersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));
    }

    private void validate(CustomersDTO dto) {
        if (dto == null
                || dto.getFirstName() == null || dto.getFirstName().isBlank()
                || dto.getLastName() == null || dto.getLastName().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "First name, last name and email are required"
            );
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }
    }
}