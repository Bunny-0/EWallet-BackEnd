package com.example.EWalletProject.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Object> accoutNotFoundException(AccountException ex){

        return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
