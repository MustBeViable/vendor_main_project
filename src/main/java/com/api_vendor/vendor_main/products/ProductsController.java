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
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer maxStock) {

        List<Products> products;

        if (name != null) {
            products = productsService.findByName(name);
        } else if (maxStock != null) {
            products = productsService.findLowStock(maxStock);
        } else if (categoryId != null) {
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

    @PostMapping("/{id}/stock/increase")
    public ProductsDTO increaseStock(@PathVariable int id,
                                     @RequestParam int amount) {
        return ProductsMapper.toDTO(productsService.increaseStock(id, amount));
    }

    @PostMapping("/{id}/stock/decrease")
    public ProductsDTO decreaseStock(@PathVariable int id,
                                     @RequestParam int amount) {
        return ProductsMapper.toDTO(productsService.decreaseStock(id, amount));
    }
}