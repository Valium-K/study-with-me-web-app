package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.SignUpForm;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.service.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;
    @Autowired private EntityManager em;

    // 실제 smtp 서비스 연동시에도 모킹으로 테스트한다.
    // 메일함 열어서 매칭해 가져오기까지 코딩이 번거롭기 때문
    // 즉, 단순히 메일을 보냈는지 까지만 테스트로 한다.
    // 실제로 잘 받았는지는 직접 한두개 케이스를 손으로...
    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면이 보이는지")
    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))                            // get sign-up
                //.andDo(print())                                             // 응답 print
                .andExpect(status().isOk())                                   // 200을 기대
                .andExpect(view().name("account/sign-up"))    // 그리고 그것이 account/sign-up view인가.
                .andExpect(model().attributeExists("signUpForm"));    // 폼이 잘 있는가
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    public void 회원가입처리_입력값오류() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "member1")
                .param("email", "emialwfn")
                .param("password", "1234")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    public void 회원가입처리_입력값정상() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "member1")
                .param("email", "email@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertNotNull(accountRepository.findDtoByEmail("email@email.com"));

        // 메일을 보냈는가
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증메일 확인 - 입력값 정상")
    public void 인증메일확인_입력값정상() throws Exception {

        Account account = Account.builder()
                .email("test@email.com")
                .password("12341234")
                .nickname("test")
                .build();

        account.generateEmailCheckToken();
        Account newAccount = accountRepository.save(account);

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/check-email"));
    }
}