package com.api_vendor.vendor_main.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================
    // 1. PROSEDUURIEN VIRHEET
    // =========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleError(
            Exception ex
    ) {

        String message = ex.getCause() != null
                ? ex.getCause().getMessage()
                : ex.getMessage();

        if (message == null) {
            message = "";
        }

        // Riittämätön varasto.
        if (message.contains("Insufficient stock")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "Insufficient stock"
                    ));
        }

        // Tuotetta ei löydy.
        if (message.contains("Product not found")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Product not found"
                    ));
        }

        // Virheellinen määrä tai tuotteen ID.
        if (message.contains("Quantity must be positive")
                || message.contains("Invalid product ID")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", message
                    ));
        }

        // Muut virheet.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal server error"
                ));
    }
}