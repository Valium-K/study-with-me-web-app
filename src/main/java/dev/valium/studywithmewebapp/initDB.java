package dev.valium.studywithmewebapp;

import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 *
 * username: test
 * email: test@test.test
 * password: testtest
 *
 */
@Component
@RequiredArgsConstructor
public class initDB {

    private final InitService initService;


    @PostConstruct
    public void init() {
        // initService.initDB();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager entityManager;
        private final AccountService accountService;
        private final PasswordEncoder passwordEncoder;

        private final String NICKNAME = "test";
        private final String EMAIL = "test@test.test";
        private final String PASSWORD = "testtest";
        private String token;

        public void initDB() {
            Account account = Account.builder()
                    .nickname(NICKNAME)
                    .email(EMAIL)
                    .password(passwordEncoder.encode(PASSWORD))
                    .build();

            entityManager.persist(account);
            accountService.sendSignUpConfirmEmail(account);
        }
    }
}