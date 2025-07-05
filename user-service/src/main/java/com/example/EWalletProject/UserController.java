package com.example.EWalletProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user")
    public User getUserByUserName(@RequestParam("userName") String userName) throws UserNotFoundException {
        return userService.getUserByUserName(userName);
    }
    @PostMapping("/createUser")
    public ResponseEntity<UserRequest> createUser(@RequestBody UserRequest user) {

        UserRequest res=userService.createUser(user);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

}
