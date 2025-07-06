package com.example.EWalletProject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpecificationRepository extends JpaRepository<Transaction, Integer> , JpaSpecificationExecutor<Transaction> {



}
