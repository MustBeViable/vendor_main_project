package com.api_vendor.vendor_main.temporal.products;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsHistoryController {

    private final ProductsHistoryService historyService;

    public ProductsHistoryController(
            ProductsHistoryService historyService
    ) {
        this.historyService = historyService;
    }

    @GetMapping("/{id}/history")
    public List<ProductsHistoryDTO> getHistory(
            @PathVariable Integer id,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime asOf
    ) {
        return historyService.getHistory(id, asOf);
    }
}
