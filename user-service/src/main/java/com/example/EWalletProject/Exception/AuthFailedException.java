package com.example.EWalletProject.Exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class AuthFailedException extends RuntimeException{


    public AuthFailedException(String message) {
        super(message);
    }
}
