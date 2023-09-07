package org.plexus.gatewayservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SecurityUtils {
    private SecurityUtils() {}

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus status) {
        return new ResponseEntity<String>(message, status);
    }
}
