package com.example.EWalletProject.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException ex) {

      Map<String ,Object> mp=new HashMap<>();
      mp.put("status", HttpStatus.BAD_REQUEST);
      mp.put("error","Account Not Found");
      mp.put("message",ex.getMessage());
      return new ResponseEntity<>(mp,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MessagePublishingException.class)
    public ResponseEntity<Object> handleMessagePublishingException(MessagePublishingException ex) {

        Map<String ,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        mp.put("error"," Brocker Issues");
        mp.put("message",ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @ExceptionHandler(TransactionNotPossibleException.class)
    public ResponseEntity<Object> handleTransactionNotPossibleException(TransactionNotPossibleException ex) {

        Map<String ,Object> mp=new HashMap<>();
        mp.put("status", HttpStatus.FORBIDDEN);
        mp.put("error"," Transaction cant be possible ");
        mp.put("message",ex.getMessage());
        return new ResponseEntity<>(mp,HttpStatus.FORBIDDEN);

    }





}
