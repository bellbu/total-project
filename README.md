## 1. Total Project 소개
- 기존 **도서 관리 애플리케이션**에 **로그인 시스템**과 **Redis 기반의 캐시** 기능을 추가하여 보안성, 성능, 확장성을 개선

- **개발 환경: Java, Spring Boot, Spring Security, JWT, JPA, MySQL, Redis, Gradle, React**

- **ERD 설계도**
  
  ![image](https://github.com/user-attachments/assets/e6027e70-797e-4c3d-b332-5d8a17fd6c95)
<br/>
<br/>
<br/>


## 2. 프로젝트 링크

  **1) 기존 버전 (도서 관리 애플리케이션)**  

  **🔗** <a href="http://ec2-43-200-177-25.ap-northeast-2.compute.amazonaws.com:8080" target="_blank">http://ec2-43-200-177-25.ap-northeast-2.compute.amazonaws.com:8080</a>
[새 탭에서 열기](https://www.google.com/){:target="_blank"}
<br/>

  **2) 개선 버전 (Total Project)**  

  **🔗** <a href="https://total-project.shop" target="_blank">https://total-project.shop</a>

  <br/>

  🔒 **테스트 계정 정보 (직접 관리자 가입 가능)**

  <aside>
  
    관리자 아이디: admin@admin.com / 비번: 1111
    
    부관리자 아이디: user@admin.com / 비번: 1111
  
  </aside>


<br/>
<br/>
<br/>

## 3. 기존 프로젝트 개선 내용

### **2-1. 웹 애플리케이션 (Library App)**

- 프론트엔드, 백엔드 **유효성 검사 강화**
    
    예) 이메일 형식, 중복 체크, 필수 입력값 체크, 특수문자 제한, 대출 중 회원 삭제 제한 등

  <br />
  <br />

  
### **2-2. 인증 & 보안 (Login System)**
- **Spring Security + JWT**를 활용한 로그인 및 인증 시스템
  
- Access Token 만료 **1분 미만 시 연장 알림창 띄워서 Refresh Token으로 재발급** 기능 구현

- 권한에 따라 **접근 제어** 기능 구현
    
    예) 비로그인 사용자 접근 차단, 부관리자 회원 목록 페이지 접근 제한
<br />

### **2-3. 캐싱 & 성능 최적화 (Redis Cache + Cursor-based Pagination)**

- 회원 조회 시 성능 비교를 위해 **조회 페이징 타입 선택** 기능 구현
    
    
    | **페이징 방식** | **평균 응답 속도 (10번 측정)** |
    | --- | --- |
    | Offset Paging | 217.6ms |
    | Cursor Paging | 17.2ms **(Offset 대비 약 13배 성능 개선)** |
    | **Redis Cache + Cursor Paging** | 5.5ms **(Offset 대비 약 40배 성능 개선)** |

<br/>

  
- **Redis 캐싱 전략 (Look Aside + Write Through)**
    
    **- 조회: Look Aside(= Cache Aside) 전략** 
    
    <aside>
      
      ✅ 캐시 HIT: 캐시에 데이터 있으면 → 캐시에서 바로 반환 

      ❌ 캐시 MISS:  캐시에 데이터 없으면 → DB 조회 → 캐시에 저장 → 반환

    </aside> <br/>



    **- 등록/수정/삭제: Write Through 전략**
    
    > 기존의 **Write Around** 전략에서 **Write Through**로 변경하여 데이터 일관성을 강화
  
    
    <aside>
      
      ✅ 데이터 변경 시 DB와 캐시에 동시 반영
  
    </aside>


