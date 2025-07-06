package com.example.EWalletProject;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class TransactionSpecification {


    public static Specification<Transaction> hasTransactionId(String id) {
        return (root, query, builder) ->
                id != null ? builder.equal(root.get("transactionId"), id) : null;
    }

    public static Specification<Transaction> hasToUser(String toUser) {
        return (root, query, builder) ->
                toUser != null ? builder.equal(root.get("toUser"), toUser) : null;
    }

    public static Specification<Transaction> hasFromUser(String fromUser) {
        return (root, query, builder) ->
                fromUser != null ? builder.equal(root.get("fromUser"), fromUser) : null;
    }

    public static Specification<Transaction> hasAmount(Double amount) { // Changed to Integer
        return (root, query, builder) ->
                amount != null ? builder.equal(root.get("amount"), amount) : null;
    }

    public static Specification<Transaction> hasTransactionStatus(String transactionStatus) {
        return (root, query, builder) ->
                transactionStatus != null ? builder.equal(root.get("transactionStatus"), transactionStatus) : null;
    }
    public static Specification<Transaction> hasCreatedAfter(LocalDateTime date) {
        return (root, query, builder) ->
                date != null ? builder.greaterThan(root.get("createdAfter"), date) : null;
    }
    public static Specification<Transaction> hasAmountRange(Double minAmount ,Double maxAmount) {
        return (root, query, builder) ->{
            List<Predicate> predicates =  new ArrayList<>();
            if(minAmount!=null){
                predicates.add(builder.greaterThan(root.get("amount"), minAmount));

            }
            if(maxAmount!=null){
                predicates.add(builder.lessThan(root.get("amount"), maxAmount));
            }

            return predicates.isEmpty() ? null : builder.and(predicates.toArray(new Predicate[0]));
        };

    }

}
