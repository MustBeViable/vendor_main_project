
package com.api_vendor.vendor_main.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(
            ResponseStatusException ex
    ) {
        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request failed";

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Invalid parameter: " + ex.getName()
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameter(
            MissingServletRequestParameterException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Missing required parameter: " + ex.getParameterName()
                ));
    }
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
