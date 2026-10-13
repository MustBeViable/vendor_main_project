package com.api_vendor.vendor_main;

import com.api_vendor.vendor_main.setup.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VendorMainApplication {


	public static void main(String[] args) {

		// Alustetaan ja tarkistetaan tietokanta ennen Spring Bootia.
		DatabaseInitializer.initialize();


		SpringApplication.run(VendorMainApplication.class, args);
	}

}
