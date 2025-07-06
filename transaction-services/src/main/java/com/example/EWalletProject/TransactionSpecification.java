package com.example.EWalletProject;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

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

    public static Specification<Transaction> hasAmount(Integer amount) { // Changed to Integer
        return (root, query, builder) ->
                amount != null ? builder.equal(root.get("amount"), amount) : null;
    }

    public static Specification<Transaction> hasTransactionStatus(String transactionStatus) {
        return (root, query, builder) ->
                transactionStatus != null ? builder.equal(root.get("transactionStatus"), transactionStatus) : null;
    }
}
