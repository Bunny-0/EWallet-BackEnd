package com.example.EWalletProject;

import org.springframework.data.jpa.domain.Specification;


public class WalletSpecification {


    public static Specification<Wallet> hasId(Integer id){

        return (root, query, criteriaBuilder) -> id!=null ? criteriaBuilder.equal(root.get("id"),id):null;
    }

    public static Specification<Wallet> hasUerName(String name){
        return (root, query, criteriaBuilder) -> name!=null?criteriaBuilder.equal(root.get("userName"),name):null;
    }

    public static Specification<Wallet> balanceGreaterThan(Integer balance){

        return (root, query, criteriaBuilder) -> balance!=null?criteriaBuilder.greaterThan(root.get("balance"),balance):null;
    }

}
