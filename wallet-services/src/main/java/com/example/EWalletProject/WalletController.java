package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletController {

    @Autowired
    WalletService walletService;

    @PostMapping("create")
    public void createWallet(@RequestParam("userName")String userName) throws JsonProcessingException {
        walletService.createWallet(userName);
        return;
    }
}
