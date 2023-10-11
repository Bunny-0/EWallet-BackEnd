package com.example.EWalletProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void createUser(UserRequest userRequest){
        User user= User.builder().userName(userRequest.getUserName()).name(userRequest.getName()).age(userRequest.getAge()).email(userRequest.getEmail()).build();
        userRepository.save(user);
    }

    public User getUserByUserName(String userName) throws UserNotFoundException {

        try {
            User user = userRepository.findByUserName(userName);
            if (user == null) {
                throw new UserNotFoundException();
            }
            return user;
        }catch (Exception e){
            throw new UserNotFoundException();
        }


    }
}
