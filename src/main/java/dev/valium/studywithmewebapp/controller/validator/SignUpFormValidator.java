package dev.valium.studywithmewebapp.controller.validator;

import dev.valium.studywithmewebapp.controller.dto.form.SignUpForm;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// org.springframework.validation.Validator 임에 주의
@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // Validation 대상 클래스스
       return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // email, nickname 중복여부 검사
        SignUpForm signUpForm = (SignUpForm) target;

        if(accountRepository.findDtoByEmail(signUpForm.getEmail()) != null) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

        if(accountRepository.findDtoByNickname(signUpForm.getNickname()) != null) {
            errors.rejectValue("nickname", "invalid.nickname",
                    new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
