package com.group.totalproject.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email); // email 존재 여부 검증
    Optional<Admin> findByEmail(String email); // email로 Admin 엔티티를 조회하는 메서드
}
