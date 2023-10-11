package com.example.EWalletProject;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class UserRequest {
    private String userName;
    private String email;
    private String name;
    private int age;
}
