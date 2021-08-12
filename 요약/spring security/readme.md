spring security
===============
### 기본
* 인증(Authentication): 해당 사용자가 본인이 맞는지를 확인하는 절차 
* 인가(Authorization): 인증된 사용자가 요청한 자원에 접근 가능한지를 결정하는 절차 
* Principal(접근 주체): 보호받는 Resource에 접근하는 대상
* Credential(비밀번호): Resource에 접근하는 대상의 비밀번호

DelegatingFilterProxy → FilterChainProxy → List 구조  

![img.png](img.png)
  

### [CSRF 공격](https://zzang9ha.tistory.com/341) 보호기능
* `@EnableWebSecurity` 어노테이션을 지정할 경우 자동으로 CSRF 보호 기능이 활성화된다.
  * 요청에 대한 CSRF 토큰을 따로 지정하거나 필터링 하지 않으면 403 error가 난다.
  * CSRF 토큰을 비교해 정상적인 HTTP method인지 구별한다.
  
### PasswordEncoder
```java
public interface PasswordEncoder {
  String encode(CharSequence rawPassword);
  boolean matches(CharSequence rawPassword, String encodedPassword);
}
```
