package dev.valium.studywithmewebapp.service;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.settings.AccountForm;
import dev.valium.studywithmewebapp.controller.dto.settings.Notifications;
import dev.valium.studywithmewebapp.controller.dto.settings.Password;
import dev.valium.studywithmewebapp.controller.dto.settings.Profile;
import dev.valium.studywithmewebapp.controller.dto.UserAccount;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.TopicOfInterest;
import dev.valium.studywithmewebapp.domain.Tag;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.repository.TopicOfInterestRepository;
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

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final TopicOfInterestRepository topicOfInterestRepository;

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

        // account객체는 단순 JAVA객체가 아닌 준영속 상태 객체이다.
        // 즉 해당 id를 JPA가 알고있기 때문에 .isNew()시에 merge()가 호출된다.
        // 이것을 잘 알지 못한다면 버그로 이어질 수 있기에
        // 아래의 메서드의 방법인 EntityManager의 1차 캐시에 넣어 사용하는것이 바람직하다.
        accountRepository.save(account);
    }

    public void updatePassword(Password password, Account account) {
        // 준영속상태의 객체이지만, 명시적으로 영속상태로 바꾸어 변경감지를 이용한 update를 한다.
        Account foundAccount = accountRepository.findByNickname(account.getNickname());
        String encodedPassword = passwordEncoder.encode(password.getPassword());

        foundAccount.setPassword(encodedPassword);
    }


    public void updateNotification(Notifications notifications, Account account) {
        Account foundAccount = accountRepository.findByNickname(account.getNickname());

        foundAccount.updateNotification(notifications);
    }

    public void updateNickname(@Valid AccountForm accountForm, Account account) {
        Account foundAccount = accountRepository.findByNickname(account.getNickname());

        foundAccount.setNickname(accountForm.getNickname());
        login(foundAccount);
    }

    public void addTopicOfInterest(Account account, Tag tag) {
        Optional<Account> foundAccount = accountRepository.findById(account.getId());

        foundAccount.ifPresent(a -> {
            TopicOfInterest topicOfInterest = TopicOfInterest.createTopicOfInterest(account, tag);

            a.getTopicOfInterests().add(topicOfInterest);
            topicOfInterestRepository.save(topicOfInterest);
            topicOfInterest.setAccount(account); // 1차 캐시 양방향 매핑용
        });
    }

    public void removeTopicOfInterest(Account account, TopicOfInterest toi) {
         Optional<Account> foundAccount = accountRepository.findById(account.getId());

         // tag컬럼은 놔두고 toi만 delete
         foundAccount.ifPresent((a) -> {
             a.getTopicOfInterests().remove(toi);
             topicOfInterestRepository.delete(toi);
         });
    }
}

