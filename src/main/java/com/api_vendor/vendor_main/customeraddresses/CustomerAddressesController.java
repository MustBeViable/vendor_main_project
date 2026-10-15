package com.api_vendor.vendor_main.customeraddresses;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customeraddresses")
public class CustomerAddressesController {

    private final CustomerAddressesService addressesService;

    public CustomerAddressesController(
            CustomerAddressesService addressesService
    ) {
        this.addressesService = addressesService;
    }

    @GetMapping
    public Page<CustomerAddressesDTO> getAll(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return addressesService.getAll(
                customerId, page, size
        );
    }

    @GetMapping("/{id}")
    public CustomerAddressesDTO getById(
            @PathVariable Integer id
    ) {
        return addressesService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAddressesDTO create(
            @RequestBody CustomerAddressesDTO dto
    ) {
        return addressesService.create(dto);
    }

    @PutMapping("/{id}")
    public CustomerAddressesDTO update(
            @PathVariable Integer id,
            @RequestBody CustomerAddressesDTO dto
    ) {
        return addressesService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Integer id
    ) {
        addressesService.delete(id);
    }
}
