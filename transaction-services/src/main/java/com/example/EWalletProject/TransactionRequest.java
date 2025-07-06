package com.example.EWalletProject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private String TransactionId;
    private  String fromUser;
    private String toUser;
    private int amount;
}
