package com.api_vendor.vendor_main.suppliers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers")
public class SuppliersController {

    private final SuppliersService suppliersService;

    public SuppliersController(
            SuppliersService suppliersService
    ) {
        this.suppliersService = suppliersService;
    }

    @GetMapping
    public Page<SuppliersDTO> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return suppliersService.getAll(name, page, size);
    }

    @GetMapping("/{id}")
    public SuppliersDTO getById(
            @PathVariable Integer id
    ) {
        return suppliersService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuppliersDTO create(
            @RequestBody SuppliersDTO dto
    ) {
        return suppliersService.create(dto);
    }

    @PutMapping("/{id}")
    public SuppliersDTO update(
            @PathVariable Integer id,
            @RequestBody SuppliersDTO dto
    ) {
        return suppliersService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        suppliersService.delete(id);
    }
}
