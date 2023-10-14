package com.example.EWalletProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public final String REDIS_PREFIX_KEY= "user::";
    public final String CREATE_WALLET = "create_wallet";

    public void createUser(UserRequest userRequest){
        User user= User.builder().userName(userRequest.getUserName()).name(userRequest.getName()).age(userRequest.getAge()).email(userRequest.getEmail()).build();
        userRepository.save(user);
        saveInCache(user);
        JSONObject walletRequest=new JSONObject();
        walletRequest.put("userName",user.getUserName());
        String message=walletRequest.toString();
        kafkaTemplate.send("create",message);

    }

    private void saveInCache(User user){
        Map map=objectMapper.convertValue(user, Map.class);
        redisTemplate.opsForHash().putAll(REDIS_PREFIX_KEY+user.getUserName(),map);
        redisTemplate.expire(REDIS_PREFIX_KEY+user.getUserName(), Duration.ofHours(12));
    }

    public User getUserByUserName(String userName) throws UserNotFoundException {

        Map map=redisTemplate.opsForHash().entries(REDIS_PREFIX_KEY+userName);
        if(map==null || map.size()==0){
            User user=userRepository.findByUserName(userName);
            if(user!=null){
                saveInCache(user);
            }
           else {
               throw new UserNotFoundException();
            }

           return user;
        }else{
            User object=objectMapper.convertValue(map,User.class);
        }
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
