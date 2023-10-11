package com.example.EWalletProject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUserName(String userName);
    List<User> findAllByUserNameAndAge(String userName,int age);
}
