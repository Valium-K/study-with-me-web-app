package dev.valium.studywithmewebapp.service;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.settings.AccountForm;
import dev.valium.studywithmewebapp.controller.dto.settings.Notifications;
import dev.valium.studywithmewebapp.controller.dto.settings.Password;
import dev.valium.studywithmewebapp.controller.dto.settings.Profile;
import dev.valium.studywithmewebapp.controller.dto.UserAccount;
import dev.valium.studywithmewebapp.domain.*;
import dev.valium.studywithmewebapp.mail.EmailMessage;
import dev.valium.studywithmewebapp.mail.EmailService;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.repository.Account_ZoneRepository;
import dev.valium.studywithmewebapp.repository.TopicOfInterestRepository;
import dev.valium.studywithmewebapp.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TopicOfInterestRepository topicOfInterestRepository;
    private final ZoneRepository zoneRepository;
    private final Account_ZoneRepository account_zoneRepository;

    // TODO select, select, insert, update ??????????????? ??????
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
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        // html??? ?????? mimeMessage
        EmailMessage email = EmailMessage.builder()
                .to(savedAccount.getEmail())
                .subject("????????? ?????? ?????? ???????????? ?????????.")
                .message("/check-email=token?token=" + savedAccount.getEmailCheckToken() +
                        "&email=" + savedAccount.getEmail())
                .build();

        emailService.sendEmail(email);
    }

    public void login(Account account) {
        // 1. account??? ????????? ?????? ????????? ??? ??? ????????? ????????????(Principal)?????? account??? ????????? ??????
        // 2. ????????? ???????????? ???????????? ???????????? ?????? ?????? ?????????, ????????? ?????? ????????? DB?????? plane password??? ??? ??? ?????? ????????????.
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

        // ????????? ?????? ??????????????? ??????
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        // ????????? throw
        if(account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        // ??? ????????? Principal ????????? ????????????.
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

        // account????????? ?????? JAVA????????? ?????? ????????? ?????? ????????????.
        // ??? ?????? id??? JPA??? ???????????? ????????? .isNew()?????? merge()??? ????????????.
        // ????????? ??? ?????? ???????????? ????????? ????????? ??? ?????????
        // ????????? ???????????? ????????? EntityManager??? 1??? ????????? ?????? ?????????????????? ???????????????.
        accountRepository.save(account);
    }

    public void updatePassword(Password password, Account account) {
        // ?????????????????? ???????????????, ??????????????? ??????????????? ????????? ??????????????? ????????? update??? ??????.
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
            topicOfInterest.setAccount(account); // 1??? ?????? ????????? ?????????
        });
    }

    public void removeTopicOfInterest(Account account, TopicOfInterest toi) {
         Optional<Account> foundAccount = accountRepository.findById(account.getId());

         // tag????????? ????????? toi??? delete
         foundAccount.ifPresent((a) -> {
             a.getTopicOfInterests().remove(toi);
             topicOfInterestRepository.delete(toi);
         });
    }

    public Set<Account_Zone> getAccount_Zones(Account account) {
        return account_zoneRepository.findAllByAccountId(account.getId());
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> foundAccount = accountRepository.findById(account.getId());

        foundAccount.ifPresent(a -> {
            Account_Zone account_zone = Account_Zone.createAccount_Zone(account, zone);

            a.getAccount_zones().add(account_zone);
            account_zoneRepository.save(account_zone);
        });
    }

    public void removeZone(Account account, Account_Zone account_zone) {
        Optional<Account> foundAccount = accountRepository.findById(account.getId());

        foundAccount.ifPresent(a -> {
            a.getAccount_zones().remove(account_zone);
            account_zoneRepository.delete(account_zone);
        });
    }

    public Account getAccount(Long id) {
        return accountRepository.findAccountById(id);
    }

    public Account getAccount(String nickname) {
        return Optional.ofNullable(
                accountRepository.findByNickname(nickname)
        ).orElseThrow(() ->
                new IllegalArgumentException(nickname + "??? ???????????? ???????????? ????????????.")
        );
    }
}

