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
@EnableCaching // 캐싱 설정을 활성화
public class RedisCacheConfig { // Redis 캐시 설정

    @Bean
    public CacheManager userCacheManager(RedisConnectionFactory redisConnectionFactory) { // RedisConfig에서 빈으로 등록된 LettuceConnectionFactory 의존성 주입
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig() // Redis 캐시에 저장할 데이터 기본 설정
                .serializeKeysWith( // Redis 캐시에 Key를 저장할 때 String 형태로 직렬화(변환)해서 저장
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith( // Redis 캐시에 Value를 저장할 때 Json 형태로 직렬화(변환)해서 저장
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<Object>(Object.class)
                        )
                )
                .entryTtl(Duration.ofSeconds(20L)); // 캐시 데이터 유효기간(TTL) 1분 설정
        
        return RedisCacheManager // RedisCacheManager 객체를 빌드하는 유틸리티 클래스
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory) // Redis 연결 팩토리를 사용하여 Redis와 통신하도록 설정
                .cacheDefaults(redisCacheConfiguration) // 앞서 정의한 캐시 설정(redisCacheConfiguration)을 기본값으로 사용
                .build(); // 최종적으로 RedisCacheManager 객체를 생성하여 반환

    }
}
