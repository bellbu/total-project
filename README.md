### **1. 웹 애플리케이션 (Library App)**

- React + Spring Boot + MySQL + JPA + Gradle 기반의 도서 관리 애플리케이션
- 프론트엔드, 백엔드 정규식 검사 및 유효성 검사 강화
    
    예) 이메일 형식, 중복 체크, 필수 입력값 체크, 특수문자 제한, 대출 중 회원 삭제 제한 등
    
- UI 개선 및 사용자 경험 향상
    
    예) 불필요한 스크롤 제거, UI 버그 수정(CSS 깨짐 해결), 가독성을 위한 문구 수정
    

### **2. 인증 & 보안 (Login System)**

- Spring Security + JWT를 활용한 로그인 및 인증 시스템
- 권한 없는 사용자의 접근 제한
- 회원 목록 조회는 관리자 권한 필요
- Access Token 만료 기간 10일 설정

### **3. 캐싱 & 성능 최적화 (Redis + Cursor-based Pagination)**

- Redis와 Cursor 기반 페이지네이션을 활용하여 “회원 조회” 속도 최적화
- 응답 속도 비교 - Postman API 호출하여 6번 측정 후 평균(소수점 첫째 자리에서 반올림)

      - Offset Paging: 281ms ⇒ Redis: 53ms

      - Offset Paging: 281ms ⇒ Cursor Paging: 100ms ⇒ Redis: 46ms

- Redis 전략(Look Aside + Write Through)
    
    - 회원 조회 시 Look Aside 전략 사용 (TTL 5분 설정)
    
    - 회원 등록, 수정, 삭제 시 Write Through 전략 사용 (기존 TTL 유지)
