package com.example.EWalletProject;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    KafkaTemplate kafkaTemplate;
    @Autowired
    NotificationService notificationService;

    @KafkaListener(topics = "notifyUser", groupId = "notification_group")
    public void notifyUser(String message) {

        try {
            notificationService.notifyUser(message);
            kafkaTemplate.send("notification_sent", message);
        }catch (Exception e) {
            kafkaTemplate.send("notification_failed", message);

        }
    }
}
