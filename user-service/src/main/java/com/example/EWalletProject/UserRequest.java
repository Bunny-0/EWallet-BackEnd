package com.example.EWalletProject;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private int id;
    private String userName;
    private String email;
    private String name;
    private String productName;
    private Status status;
    private int age;
}
