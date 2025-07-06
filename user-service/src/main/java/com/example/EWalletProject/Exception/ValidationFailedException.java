package com.example.EWalletProject.Exception;

public class ValidationFailedException extends RuntimeException{


    public ValidationFailedException(String message){
        super(message);
    }
}
