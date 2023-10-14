package com.example.EWalletProject;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findByTransactionId(String transactionId);
}
