package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class WalletOrchestrator {



    @Autowired
    KafkaTemplate kafkaTemplate;
    @Autowired
    WalletService walletService;

    @KafkaListener(topics = {"create_wallet"},groupId = "friends_group")

    public void createWallet(String message) throws Exception {
        String userName = message;
        try {
            walletService.createWallet(message);
            kafkaTemplate.send("wallet_created",userName);
        }catch (Exception e){
            kafkaTemplate.send("wallet_failed",userName);

        }

    }
    @KafkaListener(topics = {"rollBackWallet"},groupId = "friends_group")
    public void rollBackWallet(String userName) {
        try {
                walletService.rollBackWallet(userName);
                kafkaTemplate.send("wallet_rolled_back", userName);

        } catch (Exception e) {
            kafkaTemplate.send("wallet_failed", userName);
        }
    }

    @KafkaListener(topics = {"update_wallet"},groupId = "friends_group")
    public void updateWallet(String message) throws JsonProcessingException {
        try {
           String data= walletService.updateWallet(message);
            CompletableFuture<String> res = CompletableFuture.supplyAsync(() -> {
                kafkaTemplate.send("update_transaction", data);
                return "Pushed";
            });
            res.thenAccept(result -> {
                System.out.println("Kafka result: " + result);
            });
        }catch (Exception e){
            kafkaTemplate.send("rollBack_Transaction", message);
        }
    }
}

