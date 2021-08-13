package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.EmailVerificationForm;
import dev.valium.studywithmewebapp.controller.dto.SignUpForm;
import dev.valium.studywithmewebapp.controller.dto.UserAccount;
import dev.valium.studywithmewebapp.controller.validator.SignUpFormValidator;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm") // 변수명이 아닌 클래스명의 camelCase
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpFormCreate(@Valid SignUpForm signUpForm, BindingResult result) {

        if(result.hasErrors()) {
            return "account/sign-up";
        }

        // 커스텀 validator를 적용해 아래의 코드는 주석처리함
//        if(accountRepository.findByEmail(signUpForm.getEmail()) != null) {
//            result.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
//            return "account/sign-up";
//        }
//
//        if(accountRepository.findByNickname(signUpForm.getNickname()) != null) {
//            result.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
//            return "account/sign-up";
//        }

        // 회원가입 완료
        Account account = accountService.saveNewAccount(AccountDto.SignUpForm2AccountDto.convert(signUpForm));

        // 이후 바로 로그인 시켜줌
       accountService.login(account);

        return "redirect:/";
    }

    @GetMapping("/check-email")
    public String checkEmailForm(Model model) {
        EmailVerificationForm form = new EmailVerificationForm();
        form.setToken("tempToken");
        model.addAttribute(form);
        return "account/pre-check-email";
    }

    @PostMapping("/check-email")
    public String checkEmail(@Valid EmailVerificationForm form, BindingResult result, @CurrentUser Account account) {

        if(result.hasErrors()) {
            return "account/pre-check-email";
        }

        // UserAccount account = (UserAccount) authentication.getPrincipal();

        if(form.getEmail() == null || !form.getEmail().equals(account.getEmail())) {
            result.rejectValue("email", "invalid.email",
                    new Object[]{form.getEmail()}, "가입하신 이메일과 입력한 이메일이 다릅니다.");
            return "account/pre-check-email";
        }

        // TODO 이메일 인증 토큰 보내기

        return "account/check-email";
    }

    @GetMapping("/check-email-token")
    public String checkEmailTokenForm(Model model, @CurrentUser Account account) {
        EmailVerificationForm form = new EmailVerificationForm();
        // UserAccount account = (UserAccount) authentication.getPrincipal();
        form.setEmail(account.getEmail());

        model.addAttribute(form);

        return "account/check-email";
    }

    @PostMapping("/check-email-token")
    public String checkEmailToken(@Valid EmailVerificationForm form, BindingResult result, Model model, @CurrentUser Account account) {

        String token = form.getToken();

        // Account account = accountRepository.findByEmail(form.getEmail());

        String path = "account/check-email";

        // TODO: Is that possible that account is NULL??? - 스프링 시큐리티를 통한 유저정보를 가져올 때 사용하는 것이였다.
        if(account == null) {
            result.rejectValue("email", "invalid.email",
                    new Object[]{form.getEmail()}, "유효하지 않은 이메일입니다.");
            return path;
        }
        else if(!account.getEmailCheckToken().equals(token)) {
            result.rejectValue("token", "invalid.token",
                    new Object[]{form.getEmail()}, "토큰이 올바르지 않습니다.");
            return path;
        }

        // 사용자 인증까지 완료
        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());

        // 이후 바로 로그인 시켜줌
        // accountService.login(account);

        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return "account/post-check-email";
    }
}
