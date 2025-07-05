package com.example.EWalletProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class pushToKafka {


    @Autowired
    KafkaTemplate<String,String > kafkaTemplate;

    public CompletableFuture<SendResult<String,String>> sendEvents(String topic,String message){


        CompletableFuture<SendResult<String,String>> future=new CompletableFuture<>();

        kafkaTemplate.send(topic,message).addCallback(

                future::complete,
                future::completeExceptionally
        );

        return future;
    }
}
