package com.example.EWalletProject;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String userName;
    private String email;
    private String name;
    private int age;
}
