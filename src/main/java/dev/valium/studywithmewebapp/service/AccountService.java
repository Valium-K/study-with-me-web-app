package dev.valium.studywithmewebapp.service;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.settings.Password;
import dev.valium.studywithmewebapp.controller.dto.settings.Profile;
import dev.valium.studywithmewebapp.controller.dto.UserAccount;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;


    // TODO select, select, insert, update 쿼리최적화 필요
    public Account saveNewAccount(AccountDto signUpForm) {
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

        return newAccount;
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        savedAccount.generateEmailCheckToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("스터디윗미 회원 가입 인증");   // 메일 제목
        mailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail()); // 메일 본문

        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        // 1. account를 받지만 막상 로그인 할 때 사용한 접근주체(Principal)에는 account가 없음에 유의
        // 2. 이렇게 생성자를 외부에서 쓰는것은 권장 되진 않지만, 이렇게 쓰는 이유는 DB에서 plane password를 알 수 없기 때문이다.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        // 이메일 혹은 닉네임으로 조회
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        // 없다면 throw
        if(account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        // 잘 찾으면 Principal 객체를 넘겨준다.
        return new UserAccount(account);
    }

    public void completeVerification(Account account) {
        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
    }

    public void updateProfile(Account account, Profile profile) {

        account.setBio(profile.getBio());
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setProfileImage(profile.getProfileImage());

        accountRepository.save(account);
    }

    public void updatePassword(Password password, Account account) {
        Account foundAccount = accountRepository.findByNickname(account.getNickname());
        String encodedPassword = passwordEncoder.encode(password.getPassword());

        foundAccount.setPassword(encodedPassword);
    }
}

