# 스프링 부트 프로젝트
스프링 부트 프로젝트 - 스프링과 JPA 기반 웹앱
- [인프런 강좌 - 스프링과 JPA기반 웹 어플리케이션 개발](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-JPA-%EC%9B%B9%EC%95%B1)
 

# 주요 구현 기능

- [X] 회원 가입
- [X] 이메일 인증
- [X] 로그인
- [X] 로그아웃
- [X] 프로필 추가 정보 입력
- [X] 프로필 이미지 등록
- [X] 알림 설정
- [X] 패스워드 수정
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



### Open EntityManager (또는 Session) In View 필터
- JPA EntityManager(영속성 컨텍스트)를 요청을 처리하는 전체 프로세스에 바인딩 시켜주는 필터.
    - 뷰를 랜더링 할때까지 영속성 컨텍스트를 유지하기 때문에 필요한 데이터를 랜더링 하는 시점에 추가로 읽어올 수 있다. (지연 로딩, Lazy Loading)
    - 엔티티 객체 변경은 반드시 트랜잭션 안에서 할 것
        - 그래야 트랜잭션 종료 직전 또는 필요한 시점에 변경 사항을 DB에 반영

### 현재 버그
- 컨트롤러에서 데이터를 변경했다. 왜 DB에 반영되지 않았을까?
    - 트랜잭션 범위 밖에서 일어난 일이기 때문에!

- 스터디올래의 선택
    - 데이터 변경은 서비스 계층으로 위임해서 트랜잭션안에서 처리한다.
    - 데이터 조회는 리파지토리 또는 서비스를 사용한다.


## 프로필 수정 테스트

- 인증된 사용자가 접근할 수 있는 기능 테스트하기
- 실제 DB에 저장되어 있는 정보에 대응하는 인증된 Authentication이 필요하다.
- @WithMockUser로는 처리할 수 없다.

## 인증된 사용자를 제공할 커스텀 애노테이션 만들기
- @WithAccount
- https://docs.spring.io/spring-security/site/docs/current/reference/html/test.html

## 커스텀 애노테이션 생성
```java
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFacotry.class)
public @interface WithAccount {

    String value();

}
```
## SecurityContextFactory 구현
```java
public class WithAccountSecurityContextFacotry implements WithSecurityContextFactory<WithAccount> {

    // 빈을 주입 받을 수 있다.

    // Authentication 만들고 SecurityuContext에 넣어주기
        UserDetails principal = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
}
```

## 프론트 라이브러리 설치
- Cropper.JS
- npm install cropper
- npm install jquery-cropper

## Cropper.js 사용하기
```javascript
$("#profile-image-file").change(function(e) {
    if (e.target.files.length === 1) {
        const reader = new FileReader();
        reader.onload = e => {
            if (e.target.result) {
                let img = document.createElement("img");
                img.id = 'new-profile';
                img.src = e.target.result;
                img.width = 250;

                $newProfileImage.html(img);
                $newProfileImage.show();
                $currentProfileImage.hide();

                let $newImage = $(img);
                $newImage.cropper({aspectRatio: 1});
                cropper = $newImage.data('cropper');

                $cutBtn.show();
                $confirmBtn.hide();
                $resetBtn.show();
            }
        };

        reader.readAsDataURL(e.target.files[0]);
    }
});
```
### DataURL 이란?
- data: 라는 접두어를 가진 URL로 파일을 문서에 내장 시킬때 사용할 수 있다.
- 이미지를 DataURL로 저장할 수 있다.

## 패스워드 변경
- 패스워드 탭 활성화.
- 새 패스워드와 새 패스워드 확인의 값이 일치해야 한다.
- 패스워드 인코딩 할 것!
- 둘 다 최소 8자에서 최대 50자 사이.
- 사용자 정보를 변경하는 작업.
    - 서비스로 위임해서 트랜잭션 안에서 처리해야 한다.
    - 또는 Detached 상태의 객체를 변경한 다음 Repositoiry의 save를 호출해서 상태 변경 내역을 적용 할 것(Merge)

### GET /email-login
이메일을 입력할 수 있는 폼을 보여주고, 링크 전송 버튼을 제공한다.

### POST /email-login
입력받은 이메일에 해당하는 계정을 찾아보고, 있는 계정이면 로그인 가능한 링크를 이메일로 전송한다.
이메일 전송 후, 안내 메시지를 보여준다.

### GET /login-by-email
토큰과 이메일을 확인한 뒤 해당 계정으로 로그인한다.

## 타임리프 자바스크립트 템플릿
```javascript
<script type="application/javascript" th:inline="javascript"></script>
https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#javascript-inlining
Escaped: [[${variable}]]
Unescaped: [(${variable})]
네추럴 템플릿: /*[[${variable}]]*/ null;
```

## Ajax 호출시 CSRF 토큰을 전달 하는 방법
> https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-csrf-include-ajax

## 타임리프 자바스크립트 템플릿으로 Ajax 호출시 CSRF 토큰 설정
```javascript
<script type="application/javascript" th:inline="javascript">
    $(function() {
        var csrfToken = /*[[${_csrf.token}]]*/ null;
        var csrfHeader = /*[[${_csrf.headerName}]]*/ null;
        $(document).ajaxSend(function (e, xhr, options) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        });
    });
</script>
```
    
## 기존의 지역 정보 자동완성 목록에서만 선택 가능하다.
- Tagify의 whitelist를 사용한다.
> https://yaireo.github.io/tagify/#section-textarea
```javascript
var tagify = new Tagify(tagInput, {
    enforceWhitelist: true,
    whitelist: JSON.parse(document.querySelector("#whitelist").textContent),
    dropdown : {
        enabled: 1, // suggest tags after a single character input
    } // map tags
});
```

## PostgreSQL 설치 및 설정
> https://www.postgresql.org/download/
- OS에 따라 적절한 배포판 선택해서 설치.

### 설치 이후에 할 일
- psql에 접속할 것!
- 윈도 사용자 (https://www.postgresqltutorial.com/connect-to-postgresql-database/)
- 유닉스 계열 사용자
    - 커맨드 창에서 psql 입력

### DB와 유저(role) 만들고 유저에게 권한 할당하기
```shell script
Last login: Sat Feb 29 19:22:22 on ttys003
➜  ~ psql
psql (12.1)
create database testdb;
create user testuser with encrypted password 'testpass';
grant all privileges on database testdb to testuser;
```

### application-dev.properties에 DB 정보 설정
- dev 프로파일(Profile)용 설정 파일
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/testdb
spring.datasource.username=testuser
spring.datasource.password=testpass
```

## 구글 Gmail을 SMTP 서버로 사용하기
> https://support.google.com/mail/answer/185833
- App 패스워드 발급 받을 것


## application-dev.properties 설정
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=studyolledev@gmail.com // 본인 gmail 계정으로 바꾸세요.
spring.mail.password=jsxtgzwirzbvctix // 위에서 발급받은 App 패스워드로 바꾸세요.
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.starttls.enable=true
```

- 대체 서비스
- https://sendgrid.com/
- https://www.mailgun.com/
- https://aws.amazon.com/ses/
- https://gsuite.google.com/
