Thymeleaf
=========
## 속성
### th:속성
기존 태그의 속성을 Thymeleaf를 사용해 랜더링한다.
  * 가령 <a href="/" th:href="@{/} /> 같은 태그에서
    * Pure HTML: href 속성 사용
    * Thymeleaf 사용: href 가 아닌 th:href 로 매핑된다.
> 사실 권장은 한 태그에 기본속성 + Thymeleaf 속성을 같이 써주는 것이지만, th 속성만 쓰는경우가 많다.

#

### href=@{/}
servlet context 의 루트경로
* `<form th:object=${camelCaseObject}>`
    * `th:object=${camelCaseObject}`: form 태그를 채우는 객체로 사용
        * 객체의 필드는 `th:field=*{camelCaseProperty}` 형태로 참조 가능하다.
    
> th:field=*{ } 는 th:id와 th:name 속성의 값을 한번에 정의해준다.
> 즉, 속성명을 엔티티의 필드와 동일하게 맞추면 여러모로 편해진다.
> 만약 Model에 값이 들어온 채로 넘오온다면, value 속성도 같이 정의한다.

#

### justify-content-center
중앙정렬

### [Spacing](https://minaminaworld.tistory.com/136)
{sides}{breakpoint}-{size} 형식



## 예외처리
### required 이하 항목을 예외처리
```html
<input id="nickname" type="text" th:field="*{nickname}" class="form-control"
                       placeholder="닉네임을 입력하세요" aria-describedby="nicknameHelp" required minlength="3" maxlength="20">
                
```

### form-submit 전 확인
```javascript
    (function () {
        'use strict';

        window.addEventListener('load', function () {
            // Fetch all the forms we want to apply custom Bootstrap validation styles to
            var forms = document.getElementsByClassName('needs-validation');

            // Loop over them and prevent submission
            Array.prototype.filter.call(forms, function (form) {
                form.addEventListener('submit', function (event) {
                    if (form.checkValidity() === false) {
                        event.preventDefault();

                        // 현재 이벤트 이후의 전파를 막습니다.
                        event.stopPropagation();
                    }

                    // input 태그의 required 이하를 확인한다.
                    form.classList.add('was-validated')
                }, false)
            })
        }, false)
    }())
```

## Thymeleaf extras: spring-security
spring security5를 지원하는 확장팩
  * dependency: `implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5'`
  * nameSpace: `xml:sec="http://www.thymeleaf.org/extras/spring-security"`
    
## 필드에러 처리
필드에 오류가 있을 경우에만 특정 태그를 표시하고 싶다면 해당 태그에 th:if="${#fields.hasErrors('필드이름')"을 설정한다.    
th:errors="*{필드이름}"을 함께 설정하면 태그 안에 있는 문자열을 대상 필드에 관련된 에러 메시지로 치환할 수 있다.
