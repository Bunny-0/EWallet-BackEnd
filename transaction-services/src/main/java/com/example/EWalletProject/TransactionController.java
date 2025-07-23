package com.example.EWalletProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;
    @PostMapping("/create")
    public ResponseEntity<TransactionRequest> createTransaction(@RequestBody() TransactionRequest transactionRequest) throws ExecutionException, InterruptedException {
        TransactionRequest response =transactionService.createTransaction(transactionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/getTransaction")
    public ResponseEntity<List<Transaction>> getTransaction(@RequestBody TransactionFilterRequest transaction) {
        List<Transaction> response = transactionService.searchData(transaction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
