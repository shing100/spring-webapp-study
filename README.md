# 스프링 부트 프로젝트
스프링 부트 프로젝트 - 스프링과 JPA 기반 웹앱
- [인프런 강좌 - 스프링과JPA 기반 웹앱](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-JPA-%EC%9B%B9%EC%95%B1)
 

# 주요 구현 기능

- [X] 회원 가입
- [ ] 이메일 인증
- [ ] 로그인
- [ ] 로그아웃
- [ ] 프로필 추가 정보 입력
- [ ] 프로필 이미지 등록
- [ ] 알림 설정
- [ ] 패스워드 수정
- [ ] 패스워드를 잊어버렸습니다
- [ ] 관심 주제(태그) 등록
- [ ] 주요 활동 지역 등록


## 라이브러리
- 스프링 부트
- 스프링 웹 MVC
- 타임리프 (뷰 템플릿)
- 스프링 시큐리티
- 스프링 데이터 JPA
- H2
- PostgreSQL
- 롬복
- 스프링 mail
- QueryDSL
- 스프링 부트 devtools



##  리팩토링 및 테스트
리팩토링 하기전에 테스트 코드를 먼저 작성하자.
그래야 코드를 변경한 이후에 불안하지 않다.
변경한 코드가 무언가를 깨트리지 않았다는 것을 확인할 수 있다.

- 테스트 할 것
    - 폼에 이상한 값이 들어간 경우에 다시 폼이 보여지는가?
    - 폼에 값이 정상적인 경우 
    - 가입한 회원 데이터나 존재하는가?
    - 이메일이 보내지는가?

### 리팩토링
- 메소드가 너무 길지 않은가?
- 코드를 읽기 쉬운가?
- 내가 작성한 코드를 내가 읽기 어렵다면 남들에겐 훨씬 더 어렵다.
- 코드가 적절한 위치에 있는가?
- 객체들 사이의 의존 관계
- 책임이 너무 많진 않은지


## 뷰 중복 코드 제거
- 타임리프 프레그먼트 (Fragement) 사용하기
> https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#including-template-fragments

- 프레그먼트 정의
    - th:fragement
     
- 프레그먼트 사용
    - th:insert 
    - th:replace

### 뷰 중복 코드
- 메인 네비게이션
- 하단 (footer)
- 헤더 (head)


## 뷰 보안
- 네비게이션 바에 Fontawesome으로 아이콘 추가
- 이메일 인증을 마치지 않은 사용자에게 메시지 보여주기
- jdenticon으로 프로필 기본 이미지 생성하기

### NPM으로 프론트엔f드 라이브러리 설치
- npm install font-awesome
- npm install jdenticon

### 폰트어썸 아이콘 사용하기
- <i class=”fa fa-XXXX”></i>

### Jdenticon으로 아바타 생성하기
- <svg width="80" height="80" data-jdenticon-value="user127"></svg>

### 타임리프 조건문
- th:if

### 부트스트랩 경고창
```html
<div class="alert alert-warning" role="alert" th:if="${account != null && !account?.emailVerified}">
     스터디올레 가입을 완료하려면 <a href="#" th:href="@{/check-email}" class="alert-link">계정 인증 이메일을 확인</a>하세요.
</div>
```
# 스프링 시큐리티의 스프링 웹 MVC 지원
- @AuthenticationPrincipal
    - v핸들러 매개변수로 현재 인증된 Principal을 참조할 수 있다.
    
- Princial을 어디에 넣었더라?
```java    
public void login(Account account) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            account.getNickname(),
            account.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext().setAuthentication(token);
}
```

- @AuthenticationPricipal은 SpEL을 사용해서 Principal 내부 정보에 접근할 수도 있다.
- @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
- 익명 인증인 경우에는 null로 설정하고, 아닌 경우에는 account 프로퍼티를 조회해서 설정하라.

# 로그인 기억하기 (RememberMe)
- 세션이 만료 되더라도 로그인을 유지하고 싶을 때 사용하는 방법
- 쿠키에 인증 정보를 남겨두고 세션이 만료 됐을 때에는 쿠키에 남아있는 정보로 인증한다.

## 해시 기반의 쿠키
- Username
- Password
## 만료 기간
- Key (애플리케이션 마다 다른 값을 줘야 한다.)
- 치명적인 단점, 쿠키를 다른 사람이 가져가면... 그 계정은 탈취당한 것과 같다.

## 조금 더 안전한 방법은?
- 쿠키안에 랜덤한 문자열(토큰)을 만들어 같이 저장하고 매번 인증할 때마다 바꾼다.
- Username, 토큰
- 문제는, 이 방법도 취약하다. 쿠키를 탈취 당하면, 해커가 쿠키로 인증을 할 수 있고, 희생자는 쿠키로 인증하지 못한다.

## 조금 더 개선한 방법
> https://www.programering.com/a/MDO0MzMwATA.html
- Username, 토큰(랜덤, 매번 바뀜), 시리즈(랜덤, 고정)
- 쿠키를 탈취 당한 경우, 희생자는 유효하지 않은 토큰과 유효한 시리즈와 Username으로 접속하게 된다.
- 이 경우, 모든 토큰을 삭제하여 해커가 더이상 탈취한 쿠키를 사용하지 못하도록 방지할 수 있다.

### 스프링 시큐리티 설정: 해시 기반 설정
- http.rememberMe().key("랜덤한 키 값")
- https://docs.spring.io/spring-security/site/docs/current/reference/html5/#remember-me-hash-token

### 스프링 시큐리티 설정: 보다 안전한 영속화 기반 설정
```java
http.rememberMe()
        .userDetailsService(accountService)
        .tokenRepository(tokenRepository());

@Bean
public PersistentTokenRepository tokenRepository() {
    JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    jdbcTokenRepository.setDataSource(dataSource);
    return jdbcTokenRepository;
}
```

### persistent_logins 테이블 만들기
- create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)
또는 @Entity 맵핑으로 생성.
