package com.example.EWalletProject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUserName(String userName);
    User findByEmail(String email);
    @QueryHints({
            @QueryHint(name = HINT_FETCH_SIZE, value = "50")
    })
    List<User> findAllByUserNameAndAge(String userName,int age);


}
