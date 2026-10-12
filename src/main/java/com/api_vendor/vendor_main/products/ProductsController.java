package com.api_vendor.vendor_main.products;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping
    public List<ProductsDTO> getAll(
            @RequestParam(required = false) Integer categoryId) {

        List<Products> products;

        if (categoryId != null) {
            products = productsService.findByCategory(categoryId);
        } else {
            products = productsService.findAll();
        }

        return products.stream()
                .map(ProductsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductsDTO getById(@PathVariable int id) {
        return ProductsMapper.toDTO(productsService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductsDTO create(@RequestBody ProductsDTO dto) {
        return ProductsMapper.toDTO(productsService.create(dto));
    }

    @PutMapping("/{id}")
    public ProductsDTO update(@PathVariable int id, @RequestBody ProductsDTO dto) {
        return ProductsMapper.toDTO(productsService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        productsService.delete(id);
    }

    @PutMapping("/{id}/stock")
    public ProductsDTO updateStock(@PathVariable int id,
                                   @RequestParam int quantity) {
        return ProductsMapper.toDTO(productsService.updateStock(id, quantity));
    }
}