package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;

    public void createTransaction(TransactionRequest transactionRequest){
        Transaction transaction=Transaction.builder().toUser(transactionRequest.getToUser()).fromUser(transactionRequest.getFromUser()).transactionStatus(TransactionStatus.PENDING).amount(transactionRequest.getAmount()).transactionId(UUID.randomUUID().toString()).transactionTime(String.valueOf(new Date())).build();
        transactionRepository.save(transaction);
        //sedn the message to kafka to update the wallet
        //send the message to kafka to update the wallet
        JSONObject walletRequest=new JSONObject();
        walletRequest.put("fromUser",transactionRequest.getFromUser());
        walletRequest.put("toUser",transactionRequest.getFromUser());
        walletRequest.put("amount",transactionRequest.getAmount());
        walletRequest.put("transactionId",transaction.getTransactionId());
        String message=walletRequest.toString();
        kafkaTemplate.send("update_wallet",message);


    }
    @KafkaListener(topics ={"update_transaction"},groupId = "friends_group")
    public void updateTransaction(String message) throws JsonProcessingException {
        JSONObject transactionRequest=objectMapper.readValue(message,JSONObject.class);
        TransactionStatus transactionStatus=(TransactionStatus)transactionRequest.get("TransactionStatus");
        String transactionId=(String)transactionRequest.get("transactionId");
        Transaction t= transactionRepository.findByTransactionId(transactionId);
        t.setTransactionStatus(transactionStatus);
        transactionRepository.save(t);

    }
}
