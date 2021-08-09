package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.SignUpForm;
import dev.valium.studywithmewebapp.controller.validator.SignUpFormValidator;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;

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

        accountService.saveNewAccount(AccountDto.SignUpForm2AccountDto.convert(signUpForm));

        return "redirect:/";
    }


}
