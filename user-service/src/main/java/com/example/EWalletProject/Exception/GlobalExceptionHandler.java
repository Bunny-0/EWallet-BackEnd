package com.example.EWalletProject.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MessagePublishException.class)
    public ResponseEntity<Object> handleKafkaException(MessagePublishException ex) {

       Map<String,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.GATEWAY_TIMEOUT.value());
        mp.put("error", "Message Publish Exception");
        mp.put("message", ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.GATEWAY_TIMEOUT);

    }

    @ExceptionHandler(AuthFailedException.class)
    public ResponseEntity<Object> handleAuthException(MessagePublishException ex) {

        Map<String,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.GATEWAY_TIMEOUT.value());
        mp.put("error", "AuthenticationFailed");
        mp.put("message", ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(ValidationFailedException.class)
    public ResponseEntity<Object> handleValidationException(ValidationFailedException ex) {

        Map<String,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.BAD_REQUEST.value());
        mp.put("error", "Validation Failed");
        mp.put("message", ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {

        Map<String,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.NOT_FOUND.value());
        mp.put("error", "User Not Found");
        mp.put("message", ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(UserProcessingException.class)
    public ResponseEntity<Object> handleUserProcessingException(Exception ex) {

        Map<String,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        mp.put("error", "User Processing Error");
        mp.put("message", ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFound(Exception ex) {

        Map<String,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.NOT_FOUND.value());
        mp.put("error", "Product Not Found");
        mp.put("message", ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.NOT_FOUND);

    }
}
