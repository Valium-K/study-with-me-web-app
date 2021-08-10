package dev.valium.studywithmewebapp.service;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.SignUpForm;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void saveNewAccount(AccountDto signUpForm) {
        Account newAccount = accountRepository.save(Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByEmail(true)
                .build()
        );
        sendSignUpConfirmEmail(newAccount);
    }

    private void sendSignUpConfirmEmail(Account savedAccount) {
        savedAccount.generateEmailCheckToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("스터디윗미 회원 가입 인증");   // 메일 제목
        mailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail()); // 메일 본문

        javaMailSender.send(mailMessage);
    }

}
