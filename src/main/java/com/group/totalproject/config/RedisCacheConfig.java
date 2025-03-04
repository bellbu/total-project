package com.group.totalproject.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching // 캐싱 설정을 활성화(@Cacheable, @CachePut, @CacheEvict 같은 캐싱 관련 어노테이션을 사용할 수 있게 해줌)
public class RedisCacheConfig { // Redis 캐시 설정

    @Bean
    public CacheManager userCacheManager(RedisConnectionFactory redisConnectionFactory) { // RedisConfig에서 빈으로 등록된 LettuceConnectionFactory 주입받아 Redis와 연결
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration  // RedisCacheConfiguration: Redis에 데이터를 저장하는 방식을 정의하는 객체
                .defaultCacheConfig() // 기본 설정
                .serializeKeysWith( // Redis의 Key 직렬화 설정
                        RedisSerializationContext.SerializationPair.fromSerializer( // SerializationPair.fromSerializer(): 어떤 직렬화 방식을 사용할지 결정하는 메서드
                                new StringRedisSerializer() // StringRedisSerializer: Key를 저장할 때 문자열(String) 형태로 저장
                        )
                )
                .serializeValuesWith( // Value 직렬화 설정
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                // Jackson2JsonRedisSerializer: Java 객체를 JSON 문자열 형태로 직렬화해서 저장
                                // Object.class: 다양한 데이터 타입을 저장할 수 있도록 설정
                                new Jackson2JsonRedisSerializer<Object>(Object.class)
                        )
                )
                .entryTtl(Duration.ofMinutes(5)); // entryTtl: 캐시 만료 시간(TTL) 설정

        // RedisCacheManager 빌드
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory) // Redis 연결 팩토리를 사용하여 Redis와 통신하도록 설정
                .cacheDefaults(redisCacheConfiguration) // 앞서 정의한 캐시 설정(redisCacheConfiguration) 적용
                .build(); // 최종적으로 RedisCacheManager 객체를 생성하여 반환

    }
}
