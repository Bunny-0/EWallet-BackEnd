package com.example.EWalletProject.Repository;

import com.example.EWalletProject.Entity.ContractIndex;
import com.example.EWalletProject.Entity.ContractState;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElasticRepository extends ElasticsearchRepository<ContractIndex,Integer> {


    Optional<ContractIndex> findByProductName(String name);
}
