package com.example.EWalletProject;

import lombok.*;

import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@Table(name = "wallet")
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private int balance;
    private String productType;

}
