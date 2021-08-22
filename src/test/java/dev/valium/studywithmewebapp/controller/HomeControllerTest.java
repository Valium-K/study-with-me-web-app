package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.form.SignUpForm;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import static dev.valium.studywithmewebapp.controller.dto.AccountDto.SignUpForm2AccountDto.convert;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired MockMvc mockMvc; // junit이 먼저 의존성 주입을 시도하는데, 생성자 주입은 지원을 하지 않는다.
    @Autowired AccountService accountService;
    @Autowired EntityManager em;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    private void BeforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("testtest");
        signUpForm.setPassword("testtest");
        signUpForm.setEmail("test@test.test");

        accountService.saveNewAccount(convert(signUpForm));
    }

    @AfterEach
    private void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    public void 이메일로_로그인_성공() throws Exception {

        mockMvc.perform(post("/login")
                .param("username", "test@test.test")
                .param("password", "testtest")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())     // redirection은 무조건 일어난다.
                .andExpect(redirectedUrl("/"))    // 하지만 그 redirection은 root로 가야한다.
                .andExpect(authenticated().withUsername("testtest"));
    }

    @Test
    public void 닉네임으로_로그인_성공() throws Exception {

        mockMvc.perform(post("/login")
                .param("username", "testtest")
                .param("password", "testtest")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())     // redirection은 무조건 일어난다.
                .andExpect(redirectedUrl("/"))    // 하지만 그 redirection은 root로 가야한다.
                .andExpect(authenticated().withUsername("testtest"));
    }

    @Test
    public void 로그인_실패() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "failfail")
                .param("password", "failfail")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())     // redirection은 무조건 일어난다.
                .andExpect(redirectedUrl("/login?error"))    // 하지만 그 redirection은 다시 /login에 error이 추가될것이다.
                .andExpect(unauthenticated());
    }

    @Test
    public void 로그아웃() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())     // redirection은 무조건 일어난다.
                .andExpect(redirectedUrl("/"))    // 하지만 그 redirection은 root로 가야한다.
                .andExpect(unauthenticated());
    }
}