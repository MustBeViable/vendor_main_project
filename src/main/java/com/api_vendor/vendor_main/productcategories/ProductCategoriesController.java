package com.api_vendor.vendor_main.productcategories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoriesController {

    private final ProductCategoriesService productCategoriesService;

    public ProductCategoriesController(ProductCategoriesService productCategoriesService) {
        this.productCategoriesService = productCategoriesService;
    }

    @GetMapping
    public List<ProductCategoriesDTO> getAll() {
        return productCategoriesService.findAll()
                .stream()
                .map(ProductCategoriesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductCategoriesDTO getById(@PathVariable int id) {
        return ProductCategoriesMapper.toDTO(productCategoriesService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategoriesDTO createCategory(@RequestBody ProductCategoriesDTO dto) {
        ProductCategories category = ProductCategoriesMapper.toEntity(dto);
        ProductCategories saved = productCategoriesService.save(category);
        return ProductCategoriesMapper.toDTO(saved);
    }

    @PutMapping("/{id}")
    public ProductCategoriesDTO update(
            @PathVariable int id,
            @RequestBody ProductCategoriesDTO dto) {
        return ProductCategoriesMapper.toDTO(productCategoriesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        productCategoriesService.delete(id);
    }
}