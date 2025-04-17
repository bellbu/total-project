package com.group.totalproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.totalproject.dto.user.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration // Bean을 정의하고 Spring 컨테이너에 등록(Bean을 수동으로 등록)
@EnableCaching // 캐싱 설정을 활성화(@Cacheable, @CachePut, @CacheEvict 같은 캐싱 관련 어노테이션을 사용할 수 있게 해줌)
public class RedisConfig { // Redis 서버 연결 설정하고 RedisTemplate을 Bean으로 등록

    @Value("${spring.data.redis.host}") // application.yml의 host 설정 값("localhost")을 host 변수에 할당
    private String host;

    @Value("${spring.data.redis.port}")  // application.yml의 port 설정 값("6379")을 port 변수에 할당
    private int port;

    // Redis 연결 설정
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // LettuceConnectionFactory(): Redis 연결을 관리하는 객체
        // RedisStandaloneConfiguration: 단일 Redis 서버에 연결하기 위해 정보(host, port)를 설정하는 객체
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port)); // Redis에 대한 정보를 설정하여 관리하는 객체 생성
    }

    // RedisTemplate: Spring에서 Redis와 상호작용을 더 쉽게 만들어주는 템플릿 클래스
    // 역할: Redis에 데이터 저장 및 조회 / 다양한 데이터 타입 지원 / (역)직렬화 처리 / 트랜잭션 및 파이프라인 지원
    // RedisTemplate<String, List<UserResponse>>: Key는 String, Value는 List<UserResponse> 형식의 객체로 저장
    @Bean
    public RedisTemplate<String, List<UserResponse>> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<UserResponse>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key 직렬화 방식 설정: StringRedisSerializer를 사용하여 Key를 문자열(String)로 변환하여 저장
        // 직렬화 필요한 이유: Redis는 모든 데이터를 바이트(byte) 형태로 저장, Java의 객체(String, Integer, List 등)들을 직렬화 없이 바로 바이트로 저장할 수 없음
        template.setKeySerializer(new StringRedisSerializer());

        // ObjectMapper: Java 객체와 JSON 데이터간의 변환(직렬화/역직렬화) 해주는 Jackson 라이브러리
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // findAndRegisterModules(): ObjectMapper는 기본 타입만 변환 가능하고 특수한 티입은 지원하지 않으므로 사용 가능한 모듈을 찾아 ObjectMapper에 등록

        // Jackson2JsonRedisSerializer<T>: 특정 클래스 타입(T)에 대한 직렬화/역직렬화를 수행하는 직렬화 도구  ※ GenericJackson2JsonRedisSerializer: 다양한 타입을 저장할 때 적합
        // constructCollectionType(List.class, UserResponse.class): Redis에서 데이터를 가져올 때 List<UserResponse> 타입으로 변환할 수 있도록 명시적으로 설정
        Jackson2JsonRedisSerializer<List<UserResponse>> serializer = new Jackson2JsonRedisSerializer<>(objectMapper.getTypeFactory().constructCollectionType(List.class, UserResponse.class));

        // Value 직렬화 방식 설정: Value를 JSON으로 변환하여 저장하고 가져올 때 다시 List<UserResponse> 객체로 변환함(serializer)
        template.setValueSerializer(serializer);

        // Redis Hash 자료구조를 사용할 때 Key의 직렬화 방식 설정: StringRedisSerializer를 사용하여 Key를 문자열(String)로 변환하여 저장
        template.setHashKeySerializer(new StringRedisSerializer());
        // Redis Hash 자료구조를 사용할 때 Value의 직렬화 방식 설정: Value를 JSON으로 변환하여 저장하고 가져올 때 다시 List<UserResponse> 객체로 변환함(serializer)
        template.setHashValueSerializer(serializer);

        // afterPropertiesSet(): RedisTemplate의 설정을 최종적으로 적용하는 메서드
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    // RedisCacheManager 설정 (캐시 사용)
    @Bean
    public CacheManager userCacheManager(RedisConnectionFactory redisConnectionFactory) { // Redis 연결을 위해 RedisConnectionFactory 주입 받음
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration // RedisCacheConfiguration: Redis에 데이터를 저장하는 방식을 정의하는 객체
                .defaultCacheConfig() // 기본 설정
                .serializeKeysWith( // Redis의 Key 직렬화 설정
                        RedisSerializationContext.SerializationPair.fromSerializer( // SerializationPair.fromSerializer(): 어떤 직렬화 방식을 사용할지 결정하는 메서드
                                new StringRedisSerializer() // StringRedisSerializer: Key를 저장할 때 문자열(String) 형태로 저장
                        )
                ) // Redis의 Key 직렬화 설정
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        // Jackson2JsonRedisSerializer: Java 객체를 JSON 문자열 형태로 직렬화해서 저장
                        // Object.class: 다양한 데이터 타입을 저장할 수 있도록 설정
                        new Jackson2JsonRedisSerializer<>(Object.class)))
                .entryTtl(Duration.ofMinutes(3));

        return RedisCacheManager.builder(redisConnectionFactory) // 주입받은 redisConnectionFactory를 사용하여 Redis 캐시 매니저를 생성
                .cacheDefaults(cacheConfig) // 앞서 정의한 캐시 설정(cacheConfig) 적용
                .build(); // 최종적으로 RedisCacheManager 객체를 생성하여 반환
    }

}
