package com.example.EWalletProject;

import com.example.EWalletProject.Exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

public class SagaOrchestrator {

    @Autowired
    KafkaTemplate kafkaTemplate;
    @Autowired
    UserService userService;

    @KafkaListener(topics = "create_user", groupId = "user_group")
    public void createUser(UserRequest user) {

        userService.createUser(user);

    }
    @KafkaListener(topics = "wallet.created", groupId = "wallet_group")
    public void onCreatedWallet(String message) throws UserNotFoundException {

        String mailId=userService.getUserByUserName(message).getEmail();

        kafkaTemplate.send("notifyUser",mailId);
    }
    @KafkaListener(topics = "wallet.failed", groupId = "wallet_group")
    public void onFailedWallet(String message) throws UserNotFoundException {
        userService.deleteUser(message);

    }
    @KafkaListener(topics = "wallet_rollback", groupId = "wallet_group")
    public void onRollbackWallet(String message) throws UserNotFoundException {

        userService.deleteUser(message);
    }

    @KafkaListener(topics = "notification_sent", groupId = "notification_group")
    public void onNotificationSent(String message) {
        System.out.println("Notification sent successfully for: " + message);
    }
    @KafkaListener(topics = "notification_failed", groupId = "notification_group")
    public void onNotificationFailed(String message) throws UserNotFoundException{

        User user=userService.findUserByEmail(message);
        kafkaTemplate.send("rollBackWallet", user.getUserName());
        System.out.println("Notification failed for: " + message);
    }

}
