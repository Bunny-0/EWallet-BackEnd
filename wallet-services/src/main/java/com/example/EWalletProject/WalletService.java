package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.Table;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @KafkaListener(topics = {"create_wallet"},groupId = "friends_group")

    public void createWallet(String message) throws JsonProcessingException {

        JSONObject walletRequest=objectMapper.readValue(message,JSONObject.class);
        String userName=(String)walletRequest.get("userName");
        Wallet wallet=Wallet.builder().userName(userName).balance(0).build();
        walletRepository.save(wallet);


    }

    @KafkaListener(topics = {"update_wallet"},groupId = "friends_group")
    public void updateWallet(String message) throws JsonProcessingException {
        JSONObject walletRequest=objectMapper.readValue(message,JSONObject.class);
        String fromUser=(String)walletRequest.get("fromUser");
        String toUser=(String)walletRequest.get("toUser");
        int transactionAmount=(Integer)walletRequest.get("amount");
        String transactionId=(String)walletRequest.get("transactionId");

        //Check balance from user
        //deduct the senders money
        //if fail then send status as fail(if balanceis not sufficient otherwise deduct the semders money , add the receivers money send teh status success
        Wallet sendersWallet=walletRepository.findByUserName(fromUser);
        if(sendersWallet.getBalance()>=transactionAmount){
            walletRepository.updateWallet(fromUser,-1*transactionAmount);
            walletRepository.updateWallet(toUser,transactionAmount);

            //PushTokafka
            JSONObject sendToTransaction =new JSONObject();
            sendToTransaction.put("transactionId",transactionId);
            sendToTransaction.put("transactionStatus","SUCCESS");
            String SendMessage=sendToTransaction.toString();
            kafkaTemplate.send("update_transaction",SendMessage);

        }else{

            JSONObject sendToTransaction =new JSONObject();
            sendToTransaction.put("transactionId",transactionId);
            sendToTransaction.put("transactionStatus","FAILED");
            String SendMessage=sendToTransaction.toString();
            kafkaTemplate.send("update_transaction",SendMessage);

        }


    }
//    public Wallet incrementWallet(String userName,int amount){
//        Wallet oldWallet= walletRepository.findByUserName(userName);
//        int newAmount=oldWallet.getAmount()+amount;
//        int originalId=oldWallet.getId();
//
//        Wallet updateWallet=Wallet.builder().userName(userName).id(originalId).amount(newAmount).build();
//        walletRepository.save(updateWallet);
//        //another way (Through query)
////        int success =walletRepository.updateWallet(userName,amount);
//        return updateWallet;
//
//    }
//    public Wallet decrementWallet(String userName,int amount){
//        Wallet oldWallet= walletRepository.findByUserName(userName);
//        int newAmount=oldWallet.getAmount()-amount;
//        int originalId=oldWallet.getId();
//
//        Wallet updateWallet=Wallet.builder().userName(userName).id(originalId).amount(newAmount).build();
//        walletRepository.save(updateWallet);
//        return updateWallet;
//    }
}
