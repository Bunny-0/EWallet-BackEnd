package com.example.EWalletProject;

import com.example.EWalletProject.sharding.HashShardStrategy;
import com.example.EWalletProject.sharding.ShardContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Slf4j
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
           static SpecificationRepository specificationRepository;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Lock writeLock = lock.writeLock();
    Lock readLock = lock.readLock();


    @Autowired
    HashShardStrategy shardStrategy;

    public void createWallet(String message) throws Exception {


        executorService.submit(() -> {
            String userName = null;
            try {
                log.info(
                        "Received message to create wallet: {}",
                        message
                );
                JSONObject walletRequest = null;
                walletRequest = objectMapper.readValue(message, JSONObject.class);
                userName = (String) walletRequest.get("userName");
                int shard = shardStrategy.getShardId(userName);
                ShardContext.setCurrentShard(shard);
                Wallet wallet = Wallet.builder().userName(userName).balance(0).build();
                writeLock.lock();
                walletRepository.save(wallet);
                writeLock.unlock();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                ShardContext.clear();
            }
        });


    }


    public void rollBackWallet(String userName) {
        try {
            writeLock.lock();
            Wallet wallet = walletRepository.findByUserName(userName);
            if (wallet != null) {
                walletRepository.delete(wallet);
                log.info("Rolled back wallet for user: {}", userName);
            } else {
                log.warn("No wallet found for user: {}", userName);
            }
        } catch (Exception e) {
            log.error("Error rolling back wallet for user {}: {}", userName, e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }


    @Transactional
    public String updateWallet(String message) throws JsonProcessingException {

        try {
            executorService.submit(() -> {
                JSONObject walletRequest = null;
                try {
                    walletRequest = objectMapper.readValue(message, JSONObject.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                String fromUser = (String) walletRequest.get("fromUser");
                String toUser = (String) walletRequest.get("toUser");
                int transactionAmount = (Integer) walletRequest.get("amount");
                String transactionId = (String) walletRequest.get("transactionId");
                //Check balance from user
                //deduct the senders money
                //if fail then send status as fail(if balanceis not sufficient otherwise deduct the semders money , add the receivers money send teh status success
                Wallet sendersWallet = walletRepository.findByUserName(fromUser);
                if (sendersWallet.getBalance() >= transactionAmount) {

                    Wallet fromWallet = walletRepository.findByUserName(fromUser);
                    fromWallet.setBalance(fromWallet.getBalance() - transactionAmount);
                    Wallet toWallet = walletRepository.findByUserName(toUser);
                    toWallet.setBalance(toWallet.getBalance() + transactionAmount);
                    writeLock.lock();
                    walletRepository.save(fromWallet);
                    walletRepository.save(toWallet);

//            walletRepository.updateWallet(fromUser,-1*transactionAmount);
//            walletRepository.updateWallet(toUser,transactionAmount);

                    //PushTokafka
                    JSONObject sendToTransaction = new JSONObject();
                    sendToTransaction.put("transactionId", transactionId);
                    sendToTransaction.put("transactionStatus", "SUCCESS");
                    String sendMessage = sendToTransaction.toString();
                    return sendMessage;

                } else {

                    JSONObject sendToTransaction = new JSONObject();
                    sendToTransaction.put("transactionId", transactionId);
                    sendToTransaction.put("transactionStatus", "FAILED");
                    String sendMessage = sendToTransaction.toString();
                    return sendMessage;

                }
            });
        } catch (Exception e) {
            log.error("Error processing update_wallet message: {}", e.getMessage());
        } finally {
            writeLock.unlock();
        }

    return "Update wallet operation completed";
    }

    public static List<Wallet> filterData(Wallet wallet){
        Specification<Wallet> spec=Specification.where(null);

        if(wallet.getId()!=0){
            spec=spec.and(WalletSpecification.hasId(wallet.getId()==0?null:wallet.getId()));
        }
        if(wallet.getUserName()!=null){
            spec=spec.and(WalletSpecification.hasUerName(wallet.getUserName()));
        }
        return  specificationRepository.findAll();
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
