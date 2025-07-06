package com.example.EWalletProject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {
    private String transactionId;
    private String toUser;
    private String fromUser;
    private String transactionStatus;
    private Double amount;
    private Double minAmount;
    private Double maxAmount;
    private LocalDateTime createdAfter;
}
