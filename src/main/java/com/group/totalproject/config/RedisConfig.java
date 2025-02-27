package com.group.totalproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration // Bean을 정의하고 Spring 컨테이너에 등록(Bean을 수동으로 등록)
public class RedisConfig { // Redis 서버 연결 설정

    @Value("${spring.data.redis.host}") // application.yml의 host 설정 값을 host 변수에 할당
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // LettuceConnectionFactory(): Redis 연결을 관리하는 객체
        // RedisStandaloneConfiguration: Redis 단일 서버에 대한 정보(host, port)를 설정
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port)); // Redis에 대한 정보를 설정하여 관리하는 객체 생성
    }
}
