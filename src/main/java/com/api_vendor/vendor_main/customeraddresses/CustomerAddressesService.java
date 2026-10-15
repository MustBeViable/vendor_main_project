package com.api_vendor.vendor_main.customeraddresses;

import com.api_vendor.vendor_main.customers.Customers;
import com.api_vendor.vendor_main.customers.CustomersRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerAddressesService {

    private final CustomerAddressesRepository addressesRepository;
    private final CustomersRepository customersRepository;

    public CustomerAddressesService(
            CustomerAddressesRepository addressesRepository,
            CustomersRepository customersRepository
    ) {
        this.addressesRepository = addressesRepository;
        this.customersRepository = customersRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerAddressesDTO> getAll(
            Integer customerId,
            int page,
            int size
    ) {
        validatePagination(page, size);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );

        Page<CustomerAddresses> addresses;

        if (customerId != null) {
            findCustomer(customerId);

            addresses = addressesRepository.findByCustomer_Id(
                    customerId,
                    pageable
            );
        } else {
            addresses = addressesRepository.findAll(pageable);
        }

        return addresses.map(CustomerAddressesMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CustomerAddressesDTO getById(Integer id) {
        return CustomerAddressesMapper.toDTO(
                findAddress(id)
        );
    }

    @Transactional
    public CustomerAddressesDTO create(
            CustomerAddressesDTO dto
    ) {
        validate(dto);

        Customers customer = findCustomer(dto.customerId());

        CustomerAddresses address =
                CustomerAddressesMapper.toEntity(dto, customer);

        CustomerAddresses saved =
                addressesRepository.save(address);

        return CustomerAddressesMapper.toDTO(saved);
    }

    @Transactional
    public CustomerAddressesDTO update(
            Integer id,
            CustomerAddressesDTO dto
    ) {
        validate(dto);

        CustomerAddresses address = findAddress(id);
        Customers customer = findCustomer(dto.customerId());

        // Käytössä olevaa toimitusosoitetta ei voi
        // siirtää toiselle asiakkaalle.
        if (address.getCustomer().getId() != customer.getId()
                && addressesRepository.countOrdersUsingAddress(id) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Address is used by orders and cannot change customer"
            );
        }

        CustomerAddressesMapper.updateEntity(
                address,
                dto,
                customer
        );

        CustomerAddresses saved =
                addressesRepository.save(address);

        return CustomerAddressesMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Integer id) {

        CustomerAddresses address = findAddress(id);

        if (addressesRepository.countOrdersUsingAddress(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Address is used by orders and cannot be deleted"
            );
        }

        addressesRepository.delete(address);
        addressesRepository.flush();
    }

    private CustomerAddresses findAddress(Integer id) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid address ID"
            );
        }

        return addressesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer address not found"
                ));
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

    private void validate(CustomerAddressesDTO dto) {

        if (dto == null ||
                dto.customerId() == null ||
                dto.customerId() <= 0 ||
                dto.streetAddress() == null ||
                dto.streetAddress().isBlank() ||
                dto.city() == null ||
                dto.city().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID, street address and city are required"
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
