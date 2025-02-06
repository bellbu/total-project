package com.group.totalproject.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // 인터페이스를 생성 후, JpaRepository<Entity 클래스, PK 타입>를 상속하면 기본적인 CRUD 메소드가 자동으로 생성됨 / UserRepository는 스프링 빈에 등록됨
    // User는 반환타입. 유저가 없다면 null 반환됨
    Optional<User> findByName(String name);

    // 이름으로 userId 조회
    @Query("SELECT u.id FROM User u WHERE u.name = :name")
    Optional<Long> findIdByName(@Param("name") String name);

    // 동일 유저 존재 여부 확인
    boolean existsByName(String name);

}
