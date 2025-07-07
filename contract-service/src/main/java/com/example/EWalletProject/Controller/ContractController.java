package com.example.EWalletProject.Controller;


import com.example.EWalletProject.Entity.ContractIndex;
import com.example.EWalletProject.Entity.ContractState;
import com.example.EWalletProject.Service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping
    public ContractState create(@RequestBody ContractState contract) {
        return contractService.saveData(contract);
    }

    @GetMapping("/elastic/id/{id}")
    public ContractIndex getFromElastic(@PathVariable String id) {
        return contractService.searchFromElastic(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @GetMapping("/elastic/product/{productName}")
    public ResponseEntity<ContractIndex> getProductData(@PathVariable String productName) throws Exception {
        Optional<ContractIndex> data = contractService.searchByProductName(productName);
        return data.map(contract -> new ResponseEntity<>(contract, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
