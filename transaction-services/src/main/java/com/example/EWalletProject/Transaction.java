package com.example.EWalletProject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private  String transactionId= UUID.randomUUID().toString();
    private int amount;
    private String toUser;
    private String fromUser;
//    private TransactionStatus transactionStatus;
    private String transactionStatus;
    private String transactionTime;



}
