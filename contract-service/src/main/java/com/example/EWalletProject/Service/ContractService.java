package com.example.EWalletProject.Service;


import com.example.EWalletProject.Entity.ContractIndex;
import com.example.EWalletProject.Entity.ContractState;
import com.example.EWalletProject.Entity.ContractStatus;
import com.example.EWalletProject.Repository.ContractRepository;
import com.example.EWalletProject.Repository.ElasticRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.http.annotation.Contract;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContractService {

    @Autowired
    ContractRepository contractRepository;
    @Autowired
    ElasticRepository elasticRepository;

    @CircuitBreaker(name="contract-service-cb", fallbackMethod ="fallbackMethod")
    @Retry(name = " contract-service-retry",fallbackMethod = "fallbackMethod")
//    @Bulkhead(name="transactionBulkHead")
    @RateLimiter(name = "contract-service-rateLimit")
    @TimeLimiter(name = "contract-service-tL")
    public ContractState saveData(ContractState contractState){

       ContractState contractState1= contractRepository.save(contractState);
       ContractIndex contractIndex=mapToIndex(contractState1);
       elasticRepository.save(contractIndex);
       return  contractState;

    }

    public ContractIndex mapToIndex(ContractState contract) {

        ContractIndex contractIndex=new ContractIndex();

        BeanUtils.copyProperties(contract,contractIndex);
        return contractIndex;
    }
    public Optional<ContractIndex> searchFromElastic(String id) {
        Optional<ContractIndex> data= elasticRepository.findById(Integer.parseInt(id));
        return data;
    }

    public Optional<ContractIndex> searchByProductName(String name){
        Optional<ContractIndex> data=elasticRepository.findByProductName(name);
        return data;
    }
}
