package com.example.EWalletProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Builder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Properties;

@Configuration
public class Config {
@Bean
    LettuceConnectionFactory getConnection(){
        RedisStandaloneConfiguration redisStandaloneConfiguration =new RedisStandaloneConfiguration();
        LettuceConnectionFactory lettuceConnectionFactory= new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<>();
        RedisSerializer<String> redisSerializer =new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer=new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setConnectionFactory(getConnection());
        return  redisTemplate;

    }
    @Bean
    ObjectMapper getObjectMapper(){
    return new ObjectMapper();
    }

    @Bean
    Properties kafkaProperties(){
    Properties properties=new Properties();
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
    return properties;

    }
    ProducerFactory<String,String> getProducerFactory(){
        return new DefaultKafkaProducerFactory(kafkaProperties());
    }
    KafkaTemplate<String, String> getKafkaTemplate(){
        return new KafkaTemplate<>(getProducerFactory());
    }
}
