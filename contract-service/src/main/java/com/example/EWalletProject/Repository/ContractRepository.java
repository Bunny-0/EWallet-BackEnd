package com.example.EWalletProject.Repository;

import com.example.EWalletProject.Entity.ContractState;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ContractRepository extends JpaRepository<ContractState,Integer> {


    List<ContractState> findByProductName(String name);
}
