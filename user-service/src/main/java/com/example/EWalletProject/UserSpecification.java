package com.example.EWalletProject;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {


    public static Specification<User> hasId(String id){
        return (root,query,builder)-> id !=null ? builder.equal(root.get("id"),id):null;

    }

    public static Specification<User> hasName(String name ){
        return (root,query,builder)->name!=null? builder.equal(root.get("name"),name):null;
    }

    public static Specification<User> hasEmail(String email){

        return (root,query,builder)-> email!=null ? builder.equal(root.get("email"),email):null;
    }

    public static Specification<User> hasPhone(String phone){

        return (root,query,builder)-> phone!=null ? builder.equal(root.get("phone"),phone):null;
    }

    public static Specification<User> hasAgeGreaterThan(Integer age){

        return (root,query,builder)-> age!=null ? builder.greaterThan(root.get("age"),age):null;
    }

    public static Specification<User> hasAgeLessThan(Integer age){

        return (root,query,builder)-> age!=null ? builder.lessThan(root.get("age"),age):null;
    }
    public static Specification<User> hasAgeRange(Integer minAge,Integer maxAge){
        return (root,query,builder)->{
            List<Predicate> predicates =  new ArrayList<>();
            if(minAge!=null){
                predicates.add(builder.greaterThan(root.get("age"), minAge));

            }
            if(maxAge!=null){
                predicates.add(builder.lessThan(root.get("age"), maxAge));
            }
            return predicates.isEmpty() ? null : builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
