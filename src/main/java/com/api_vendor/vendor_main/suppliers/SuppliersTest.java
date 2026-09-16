package com.api_vendor.vendor_main.suppliers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SuppliersTest implements CommandLineRunner {

    @Override
    public void run(String... args) {

        System.out.println("=== SUPPLIERS TESTI ALKAA ===");

        System.out.println(suppliersRepository.findAll());

        Suppliers supplier = new Suppliers();
        supplier.setName("Testi toimittaja");

        Suppliers saved = suppliersRepository.save(supplier);

        System.out.println("Tallennettu ID: " + saved.getSuppliersID());

        System.out.println(
                suppliersRepository.findById(saved.getSuppliersID())
        );

        System.out.println("=== SUPPLIERS TESTI VALMIS ===");
    }
    private final SuppliersRepository suppliersRepository;

    public SuppliersTest(SuppliersRepository suppliersRepository) {
        this.suppliersRepository = suppliersRepository;
    }
}