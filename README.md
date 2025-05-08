## 1. Total Project 소개
- 기존 **도서 관리 애플리케이션**에 **로그인 시스템**과 **Redis 기반의 캐시** 기능을 추가하여 보안성, 성능, 확장성을 개선

- **개발 환경: Java, Spring Boot, Spring Security, JWT, JPA, MySQL, Redis, Gradle, React**

- **ERD 설계도**
![image](https://github.com/user-attachments/assets/e6027e70-797e-4c3d-b332-5d8a17fd6c95)<br/>

 
## 2. 기존 프로젝트 개선 내용

### **2-1. 웹 애플리케이션 (Library App)**

- 프론트엔드, 백엔드 **유효성 검사 강화**
    
    예) 이메일 형식, 중복 체크, 필수 입력값 체크, 특수문자 제한, 대출 중 회원 삭제 제한 등
  <br />
  <br />
  
### **2-2. 인증 & 보안 (Login System)**
- **Spring Security + JWT**를 활용한 로그인 및 인증 시스템
- 권한에 따라 **접근 제어** 기능 구현
    
    예) 비로그인 사용자 접근 차단, 부관리자 회원 목록 페이지 접근 제한
    
- **Refresh Token**을 이용한 **Access Token** 재발급 기능 구현 (TTL : Access Token - 3분, Refresh Token - 30분)
<br />

### **2-3. 캐싱 & 성능 최적화 (Redis Cache + Cursor-based Pagination)**

- 회원 조회 시 성능 비교를 위해 **세가지 페이징 방식 선택 가능**하도록 구현
    
    
    | **페이징 방식** | **평균 응답 속도 (10번 측정)** |
    | --- | --- |
    | Offset Paging | 281ms |
    | Cursor Paging | 100ms **(Offset 대비 4배 성능 개선)** |
    | **Redis Cache + Cursor Paging** | 46ms **(Offset 대비 2배 성능 개선)** |
- **Redis 캐싱 전략 (Look Aside + Write Through)**
    
    - 조회: **Look Aside 전략** (캐시에 없으면 DB → Redis 저장 → 반환)
    
    - 등록/수정/삭제:  **Write Through 전략** (DB 및 캐시 동시 반영)
    
    -  TTL: 3분 (페이지 단위로 설정하여 효율적인 갱신 유지)
