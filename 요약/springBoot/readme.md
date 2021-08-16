springBoot
==========
### Basic
* `@ModelAttribute`는 파라미터로 받을 때는 생략가능
  * 기본생성자를 만든 후 setter를 이용해 설정하기에 기본생성자가 없으면 `nullpointerexception`이다.
* BindingResult는 Errors interface를 상속받은것.
* 컨트롤러에서 `return redirect:CURRENT_URL`은 폼 submit 후 form이 재전송 되는것을 막는 용도로 사용 할 수 있다. 
### Validator
#### Custom Validator 사용
```java
// org.springframework.validation.Validator 임에 주의
@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // Validation 대상 클래스
       return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // 예외처리 시작
        SignUpForm signUpForm = (SignUpForm) target;

        if(accountRepository.findByEmail(signUpForm.getEmail()) != null) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

        if(accountRepository.findByNickname(signUpForm.getNickname()) != null) {
            errors.rejectValue("nickname", "invalid.nickname",
                    new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;

    // custom validator 적용
    @InitBinder("signUpForm") // 변수명이 아닌 클래스명의 camelCase
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @PostMapping("/sign-up")
    public String signUpFormCreate(@Valid SignUpForm signUpForm, BindingResult result) {
        if(result.hasErrors()) {
            return "account/sign-up";
        }

        return "redirect:/";
    }
}

```

#### Controller에 직접 코딩
```java
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    // 모델을 잘 넘겨받는다 가정. 위 코드와 최대한 비슷하게 비교하기위해.
    @PostMapping("/sign-up")
    public String signUpFormCreate(@Valid SignUpForm signUpForm, BindingResult result) {

        if(accountRepository.findByEmail(signUpForm.getEmail()) != null) {
            result.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
            return "account/sign-up";
        }

        if(accountRepository.findByNickname(signUpForm.getNickname()) != null) {
            result.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
            return "account/sign-up";
        }

        return "redirect:/";
    }
}

```
