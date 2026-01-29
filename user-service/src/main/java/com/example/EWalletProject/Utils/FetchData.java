package com.example.EWalletProject.Utils;


import com.example.EWalletProject.ContractIndex;
import com.example.EWalletProject.ContractStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class FetchData {

    @Autowired
    RestTemplate restTemplate;
    @CircuitBreaker(name = "contractServiceCB",fallbackMethod = "fallbackFetchContract")
    @RateLimiter(name = "contractServiceRateLimiter", fallbackMethod = "contractServiceRL")
    @Retry(name = "contractServiceRetry")
    public ContractIndex fetchContract(String productName) {
        String url = "http://localhost:9045/api/contracts/elastic/product/" + productName;

        ResponseEntity<ContractIndex> response = restTemplate.getForEntity(url, ContractIndex.class);
        ContractIndex body = response.getBody();
        if (body != null) {
            return body;
        } else {
            throw new RuntimeException("Product not found in external service: " + productName);
        }
    }

    public ContractIndex fallbackFetchContract(String productName, Throwable ex) {
        ContractIndex res=new ContractIndex();
        res.setStatus(ContractStatus.NOT_FOUND);
        res.setProductName(productName);
        return res;

    }
    public ContractIndex contractServiceRL(String productName, Throwable ex) {

        ContractIndex res=new ContractIndex();
        res.setStatus(ContractStatus.INACTIVE);
        res.setProductName(productName);
        return res;

    }
}
