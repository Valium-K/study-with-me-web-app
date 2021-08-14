Test case 
==========
## with spring security
* POST요청은 반드시 `.with(csrf())` 를 추가해야한다.
* 인증여부는 .andExpect( [ authenticated() | unauthenticated() ] )로 확인 가능하다.
* 임의로 로그인 된 사용자가 필요한 경우는 `@WithMockUser` 를 사용한다.
  
　
## [MockMVC](https://elevatingcodingclub.tistory.com/61)
웹 애플리케이션에서 컨트롤러를 테스트할 때, 서블릿 컨테이너를 모킹하기 위해 @WebMvcTest 나 @AutoConfigureMockMvc 를 사용한다.

### @WebMvcTest
`@SpringBootTest`와 함께 쓸수 없다.

### @AutoConfigureMockMvc
* `@SpringBootTest`는 기본 의존 속성은`@SpringBootTest(webEnvironment=WebEnvironment.MOCK)` 와 같다. 이것을 주입받기위해 `@AutoConfigureMockMvc`를 사용한다.
    
### 기본문법
* 리다이렉트 응답은 .andExpect(status().is3xxRedirection()) 으로 확인한다.
* 리다이렉트 URL은 .andExpect(redirectedUrl()) 로 확인한다.
