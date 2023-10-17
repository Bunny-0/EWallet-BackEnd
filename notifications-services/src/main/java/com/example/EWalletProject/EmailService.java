package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SimpleMailMessage simpleMailMessage;

    @KafkaListener(topics = {"send_email"},groupId = "friends_group")
    public void sendEmailMessage(String message) throws JsonProcessingException {

        //Decoding the message to the JSON Obj
        //User email...message

        JSONObject emailRequest= objectMapper.readValue(message,JSONObject.class);
        //Get the email from the json obj
        String email=(String)emailRequest.get("email");
        String messageBody=(String)emailRequest.get("message");

        simpleMailMessage.setTo(email);
        simpleMailMessage.setText(messageBody);
        simpleMailMessage.setSubject("Transaction Mail");
        simpleMailMessage.setFrom("wocharlog4@gmail.com");
        javaMailSender.send(simpleMailMessage);

    }
}
