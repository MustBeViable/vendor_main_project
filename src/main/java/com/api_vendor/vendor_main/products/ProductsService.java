
package com.api_vendor.vendor_main.products;

import com.api_vendor.vendor_main.productcategories.ProductCategories;
import com.api_vendor.vendor_main.productcategories.ProductCategoriesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final ProductCategoriesRepository productCategoriesRepository;

    public ProductsService(ProductsRepository productsRepository,
                           ProductCategoriesRepository productCategoriesRepository) {
        this.productsRepository = productsRepository;
        this.productCategoriesRepository = productCategoriesRepository;
    }

    @Transactional(readOnly = true)
    public List<Products> findAll() {
        return productsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Products> findByCategory(Integer categoryId) {
        return productsRepository.findAllByCategory_CategoryID(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Products> findByName(String name) {
        return productsRepository.findByNameStartingWith(name);
    }

    @Transactional(readOnly = true)
    public List<Products> findLowStock(int threshold) {
        return productsRepository.findByStockQuantityLessThanEqual(threshold);
    }

    @Transactional(readOnly = true)
    public Products findById(int id) {
        return productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));
    }

    @Transactional
    public Products create(ProductsDTO dto) {
        Products product = ProductsMapper.toEntity(dto);
        attachCategory(product, dto.getCategoryId());
        return productsRepository.save(product);
    }

    @Transactional
    public Products update(int id, ProductsDTO dto) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setSupplierId(dto.getSupplierId());

        attachCategory(product, dto.getCategoryId());

        return product;
    }

    @Transactional
    public void delete(int id) {
        productsRepository.deleteById(id);
    }

    @Transactional
    public Products increaseStock(int id, int amount) {
        productsRepository.stockIncrease(id, amount);
        return productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));
    }

    @Transactional
    public Products decreaseStock(int id, int amount) {
        productsRepository.stockDecrease(id, amount);
        return productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));
    }

    private void attachCategory(Products product, Integer categoryId) {
        if (categoryId == null) return;
        ProductCategories category = productCategoriesRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product category not found"
                ));
        product.setCategory(category);
    }
}
