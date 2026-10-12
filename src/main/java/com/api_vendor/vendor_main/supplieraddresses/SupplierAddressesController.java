
package com.api_vendor.vendor_main.supplieraddresses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplieraddresses")
public class SupplierAddressesController {

    private final SupplierAddressesService addressesService;

    public SupplierAddressesController(
            SupplierAddressesService addressesService
    ) {
        this.addressesService = addressesService;
    }

    @GetMapping
    public List<SupplierAddressesDTO> getAll(
            @RequestParam(required = false) Integer supplierId
    ) {
        return addressesService.getAll(supplierId);
    }

    @GetMapping("/{id}")
    public SupplierAddressesDTO getById(
            @PathVariable Integer id
    ) {
        return addressesService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SupplierAddressesDTO> create(
            @RequestBody SupplierAddressesDTO dto
    ) {
        SupplierAddressesDTO created = addressesService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public SupplierAddressesDTO update(
            @PathVariable Integer id,
            @RequestBody SupplierAddressesDTO dto
    ) {
        return addressesService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        addressesService.delete(id);
    }
}
