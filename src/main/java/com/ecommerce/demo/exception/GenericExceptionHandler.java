package com.ecommerce.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<String> handleShoppingCartItemNotFoundException(GenericException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
