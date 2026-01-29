package com.example.EWalletProject.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Column;

@Document(indexName = "contracts")
@Getter
@Setter
public class ContractIndex {

    @Id
    private String id;
    @Column(name = "contract_number", nullable = false)
    private String contractNumber;
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


    public ContractIndex(String id, String contractNumber, String productName, double interestRate, double monthlyLimit, double dailyLimit, double yearlyLimit, ContractStatus status) {
        this.id = id;
        this.contractNumber = contractNumber;
        this.productName = productName;
        this.interestRate = interestRate;
        this.monthlyLimit = monthlyLimit;
        this.dailyLimit = dailyLimit;
        this.yearlyLimit = yearlyLimit;
        this.status = status;
    }

    public ContractIndex() {

    }
}
