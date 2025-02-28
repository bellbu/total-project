package com.group.totalproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.totalproject.dto.user.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.List;

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

    @Bean
    public RedisTemplate<String, List<UserResponse>> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<UserResponse>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // ✅ Key Serializer (문자열)
        template.setKeySerializer(new StringRedisSerializer());

        // ✅ ObjectMapper 설정 (최신 버전 호환)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // ✅ LocalDate, LocalDateTime 자동 지원

        // ✅ GenericJackson2JsonRedisSerializer 사용 (더 안정적인 직렬화)
        Jackson2JsonRedisSerializer<List<UserResponse>> serializer = new Jackson2JsonRedisSerializer<>(objectMapper.getTypeFactory().constructCollectionType(List.class, UserResponse.class));

        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

}
