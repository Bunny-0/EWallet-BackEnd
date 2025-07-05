package com.example.EWalletProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    pushToKafka pushToKafka;


    public final String REDIS_PREFIX_KEY = "user::";
    public final String CREATE_WALLET = "create_wallet";


    @Transactional
    public UserRequest createUser(UserRequest userRequest) {
        User user = User.builder().userName(userRequest.getUserName()).name(userRequest.getName()).productName(userRequest.getProductName()).age(userRequest.getAge()).email(userRequest.getEmail()).build();
        CompletableFuture<ContractIndex> futureData = fetchContract(userRequest.getProductName());

        futureData.thenAccept(contractIndex -> {
            if (!contractIndex.getStatus().equals(ContractStatus.ACTIVE)) {
                throw new RuntimeException("Product Data not valid");
            }
            userRepository.save(user);
            saveInCache(user);
            JSONObject walletRequest = new JSONObject();
            walletRequest.put("userName", user.getUserName());
            String message = walletRequest.toString();
            OutboxEvent event = new OutboxEvent();
            event.setEventType("create_wallet");
            event.setPayload(message);
            event.setStatus("PENDING");

            outboxEventRepository.save(event);


        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        userRequest.setId(user.getId());
        userRequest.setStatus(Status.PROCESSING);

        return userRequest;

    }


    @Async
    public CompletableFuture<ContractIndex> fetchContract(String productName) {
        String url = "http://localhost:9045/api/contracts/elastic/product/" + productName;

        ResponseEntity<ContractIndex> response = restTemplate.getForEntity(
                url,
                ContractIndex.class
        );

        return CompletableFuture.completedFuture(response.getBody());


    }

    @Scheduled(fixedDelay = 5000)
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus("PENDING");
        System.out.println("Processing " + pendingEvents.toString() + " pending events...");
        for (OutboxEvent event : pendingEvents) {
            try {
                pushToKafka.sendEvents(event.getEventType(), event.getPayload()).get(); // sync send
                event.setStatus("SENT");
            } catch (Exception e) {
                System.err.println("❌ Kafka failed: " + e.getMessage());
                event.setStatus("FAILED");
            }

            outboxEventRepository.save(event);
        }
    }


    private void saveInCache(User user) {
        Map map = objectMapper.convertValue(user, Map.class);
        redisTemplate.opsForHash().putAll(REDIS_PREFIX_KEY + user.getUserName(), map);
        redisTemplate.expire(REDIS_PREFIX_KEY + user.getUserName(), Duration.ofHours(12));
    }

    public User getUserByUserName(String userName) throws UserNotFoundException {

        Map map = redisTemplate.opsForHash().entries(REDIS_PREFIX_KEY + userName);
        if (map == null || map.size() == 0) {
            User user = userRepository.findByUserName(userName);
            if (user != null) {
                saveInCache(user);
            } else {
                throw new UserNotFoundException();
            }

            return user;
        } else {
            User object = objectMapper.convertValue(map, User.class);
        }
        try {
            User user = userRepository.findByUserName(userName);
            if (user == null) {
                throw new UserNotFoundException();
            }
            return user;
        } catch (Exception e) {
            throw new UserNotFoundException();
        }


    }
}
