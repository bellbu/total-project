package com.group.libraryapp.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // 인터페이스를 생성 후, JpaRepository<Entity 클래스, PK 타입>를 상속하면 기본적인 CRUD 메소드가 자동으로 생성됨 / UserRepository는 스프링 빈에 등록됨

    Optional<User> findByName(String name); // User는 반환타입. 유저가 없다면 null 반환됨

}
