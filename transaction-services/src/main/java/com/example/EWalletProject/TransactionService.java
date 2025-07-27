package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Slf4j
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

    ReadWriteLock lock=new ReentrantReadWriteLock();
    Lock writeLock = lock.writeLock();

    @Transactional
    public TransactionRequest createTransaction(TransactionRequest transactionRequest) throws ExecutionException, InterruptedException {
        Transaction transaction = Transaction.builder().toUser(transactionRequest.getToUser()).fromUser(transactionRequest.getFromUser()).transactionStatus("PENDING").amount(transactionRequest.getAmount()).transactionId(UUID.randomUUID().toString()).transactionTime(String.valueOf(new Date())).build();

        Transaction savedData;

        writeLock.lock();
        try {
            savedData = transactionRepository.save(transaction);
        } finally {
            writeLock.unlock(); // Ensures lock is always released
        }
        //send the message to kafka to update the wallet
        //send the message to kafka to update the wallet
        JSONObject walletRequest = new JSONObject();
        walletRequest.put("fromUser", transactionRequest.getFromUser());
        walletRequest.put("toUser", transactionRequest.getToUser());
        walletRequest.put("amount", transactionRequest.getAmount());
        walletRequest.put("transactionId", transaction.getTransactionId());
        String message = walletRequest.toString();
       CompletableFuture<String> completableFuture= CompletableFuture.supplyAsync(()-> {
           try {
               kafkaTemplate.send("update_wallet", message).get();
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           } catch (ExecutionException e) {
               throw new RuntimeException(e);
           }
           return "Published to update_wallet topic successfully.";
       });
       log.info(completableFuture.get());

        TransactionRequest res = TransactionRequest.builder().amount(savedData.getAmount())
                .fromUser(savedData.getFromUser())
                .toUser(savedData.getToUser())
                .TransactionId(savedData.getTransactionId()).build();
        return res;


    }

    @KafkaListener(topics = {"update_transaction"}, groupId = "friends_group")
    @Transactional
    public void updateTransaction(String message) throws JsonProcessingException, ExecutionException, InterruptedException {
        JSONObject transactionRequest = objectMapper.readValue(message, JSONObject.class);
        String transactionStatus = (String) transactionRequest.get("transactionStatus");
        String transactionId = (String) transactionRequest.get("transactionId");
        Transaction t = transactionRepository.findByTransactionId(transactionId);
        t.setTransactionStatus(transactionStatus);
        Transaction savedData;

        writeLock.lock();
        try {
            savedData = transactionRepository.save(t);
        } finally {
            writeLock.unlock();
        }
        //call notification service and send mails
        callNotificationService(t);

    }
    @KafkaListener(topics = {"rollBack_Transaction"}, groupId = "friends_group")
    public void rollBackTransaction(String transactionId) {
        try {
            writeLock.lock();
            Transaction transaction = transactionRepository.findByTransactionId(transactionId);
            if (transaction != null) {
                transactionRepository.delete(transaction);
                log.info("Rolled back transaction for ID: {}", transactionId);
            } else {
                log.warn("No transaction found for ID: {}", transactionId);
            }
        } catch (Exception e) {
            log.error("Error rolling back transaction for ID {}: {}", transactionId, e.getMessage());
        } finally {
            writeLock.unlock();
        }
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

    public void callNotificationService(Transaction transaction) throws ExecutionException, InterruptedException {
        //Fetch IS EMAIL FROM USER SERVICE
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        String fromUser = transaction.getFromUser();
        String toUser = transaction.getToUser();
        String transactionId = transaction.getTransactionId();
        HttpEntity httpEntity = new HttpEntity(new HttpHeaders());
        Callable<JSONObject> callable1 = () -> {
            URI url = URI.create("http://localhost:8076/user?userName=" + fromUser);
            return restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
        };
        Callable<JSONObject> callable2 = () -> {
            URI url = URI.create("http://localhost:8076/user?userName=" + toUser);
            return restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
        };
        Future<JSONObject> future1=executorService.submit(callable1);
        Future<JSONObject> future2=executorService.submit(callable2);
        JSONObject fromUserObject =future1.get();
        JSONObject toUserObject = future2.get();
        String senderEmail = (String) fromUserObject.get("email");
        String senderName = (String) fromUserObject.get("name");

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
