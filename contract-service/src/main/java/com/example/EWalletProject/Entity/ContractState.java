package com.example.EWalletProject.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "contracts",
indexes = {
        @Index(name = "contractIdx",columnList = "id"),
        @Index(name="contractNumIdx",columnList = "contractNumber")
})
@Getter
@Setter
public class ContractState {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "contract_number", nullable = false)
        private String contractNumber;

        public ContractState(String contractNumber, String productName, double interestRate, double monthlyLimit, double dailyLimit, double yearlyLimit) {
                this.contractNumber = contractNumber;
                this.productName = productName;
                this.interestRate = interestRate;
                this.monthlyLimit = monthlyLimit;
                this.dailyLimit = dailyLimit;
                this.yearlyLimit = yearlyLimit;
        }

        @Column(name = "product_name", nullable = false)
        private String productName;
        @Column(name = "interest_rate", nullable = false)
        private double interestRate;
        @Column(name = "monthly_limit", nullable = false)
        private double monthlyLimit;
        @Column(name = "daily_limit", nullable = false)
        private double dailyLimit;
        @Column(name = "yearly_limit", nullable = false)
        private double yearlyLimit;

        @Column(name = "status")
        private ContractStatus status;


        public ContractState() {}



}
