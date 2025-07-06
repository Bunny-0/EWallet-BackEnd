package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TransactionSpecification transactionSpecification;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SpecificationRepository sp;

    public TransactionRequest createTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = Transaction.builder().toUser(transactionRequest.getToUser()).fromUser(transactionRequest.getFromUser()).transactionStatus("PENDING").amount(transactionRequest.getAmount()).transactionId(UUID.randomUUID().toString()).transactionTime(String.valueOf(new Date())).build();
        Transaction savedData = transactionRepository.save(transaction);
        //send the message to kafka to update the wallet
        //send the message to kafka to update the wallet
        JSONObject walletRequest = new JSONObject();
        walletRequest.put("fromUser", transactionRequest.getFromUser());
        walletRequest.put("toUser", transactionRequest.getToUser());
        walletRequest.put("amount", transactionRequest.getAmount());
        walletRequest.put("transactionId", transaction.getTransactionId());
        String message = walletRequest.toString();
        kafkaTemplate.send("update_wallet", message);
        TransactionRequest res = TransactionRequest.builder().amount(savedData.getAmount())
                .fromUser(savedData.getFromUser())
                .toUser(savedData.getToUser())
                .TransactionId(savedData.getTransactionId()).build();
        return res;


    }

    @KafkaListener(topics = {"update_transaction"}, groupId = "friends_group")
    public void updateTransaction(String message) throws JsonProcessingException {
        JSONObject transactionRequest = objectMapper.readValue(message, JSONObject.class);
        String transactionStatus = (String) transactionRequest.get("transactionStatus");
        String transactionId = (String) transactionRequest.get("transactionId");
        Transaction t = transactionRepository.findByTransactionId(transactionId);
        t.setTransactionStatus(transactionStatus);
        transactionRepository.save(t);

        //call notification service and send mails
        callNotificationService(t);

    }


    public List<Transaction> searchData(TransactionFilterRequest transaction) {
        Specification<Transaction> spec = Specification.where(null); // start safely with null

        if (transaction.getToUser() != null) {
            spec = spec.and(TransactionSpecification.hasToUser(transaction.getToUser()));
        }

        if (transaction.getFromUser() != null) {
            spec = spec.and(TransactionSpecification.hasFromUser(transaction.getFromUser()));
        }

        if (transaction.getTransactionStatus() != null) {
            spec = spec.and(TransactionSpecification.hasTransactionStatus(transaction.getTransactionStatus()));
        }

        if (transaction.getTransactionId() != null) {
            spec = spec.and(TransactionSpecification.hasTransactionId(transaction.getTransactionId()));
        }

        if (transaction.getAmount() !=null) {
            spec = spec.and(TransactionSpecification.hasAmount(transaction.getAmount()));
        }
        if(transaction.getCreatedAfter()!=null){
            spec=spec.and(TransactionSpecification.hasCreatedAfter(transaction.getCreatedAfter()));
        }
        if(transaction.getMinAmount() != null || transaction.getMaxAmount() != null) {
            spec = spec.and(TransactionSpecification.hasAmountRange(transaction.getMinAmount(), transaction.getMaxAmount()));
        }


        return sp.findAll(spec);
    }

    public void callNotificationService(Transaction transaction) {
        //Fetch IS EMAIL FROM USER SERVICE
        String fromUser = transaction.getFromUser();
        String toUser = transaction.getToUser();
        String transactionId = transaction.getTransactionId();
        HttpEntity httpEntity = new HttpEntity(new HttpHeaders());
        URI url = URI.create("http://localhost:8076/user?userName=" + fromUser);
        JSONObject fromUserObject = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
        String senderEmail = (String) fromUserObject.get("email");
        String senderName = (String) fromUserObject.get("name");

        url = URI.create("http://localhost:8076/user?userName=" + toUser);
        JSONObject toUserObject = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
        String receiverEmail = (String) toUserObject.get("email");
        String recieverName = (String) toUserObject.get("name");

        //SEND THE EMAIL AND MESSAGE TO NOTIFICATIONS-SERVICE VIA KAFKA

        JSONObject emailRequest = new JSONObject();
        emailRequest.put("email", senderEmail);
        String SendersMessageBody = String.format("Hi %s the transaction with transactionid %s has been %s of Rs %d", senderName, transactionId, transaction.getTransactionStatus(), transaction.getAmount());
        emailRequest.put("message", SendersMessageBody);

        String message = emailRequest.toString();
        kafkaTemplate.send("send_email", message);
        if (transaction.getTransactionStatus().equals("FAILED")) {
            return;
        }
        emailRequest.put("email", senderEmail);
        String receiverMessageBody = String.format("Hi %s you have received money %d from %s", recieverName, transaction.getAmount(), senderName);
        message = emailRequest.toString();
        kafkaTemplate.send("send_email", message);


    }
}
