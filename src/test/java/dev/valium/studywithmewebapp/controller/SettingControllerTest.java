package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.controller.dto.form.SignUpForm;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import dev.valium.studywithmewebapp.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("tempUser");
        signUpForm.setEmail("temp@temp.temp");
        signUpForm.setPassword("tmeptemp");
        accountService.saveNewAccount(AccountDto.SignUpForm2AccountDto.convert(signUpForm));
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "tempUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void 프로필_수정_입력값_정상() throws Exception {
        String bio = "짧은 소개를 입력.";

        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                    .param("bio", bio)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account tempUser = accountRepository.findByNickname("tempUser");
        assertEquals(bio, tempUser.getBio());
    }

    @WithUserDetails(value = "tempUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void 프로필_수정_입력값_초과_에러() throws Exception {
        String bio = "길게 입력한 소개.길게 입력한 소개.길게 입력한 소개.길게 입력한 소개.길게 입력한 소개.길게 입력한 소개.길게 입력한 소개.";

        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account tempUser = accountRepository.findByNickname("tempUser");
        assertNull(tempUser.getBio());
    }

    @WithUserDetails(value = "tempUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void 프로필_수정_폼_get() throws Exception {
        String bio = "짧은 소개를 입력.";

        mockMvc.perform(get(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

        Account tempUser = accountRepository.findByNickname("tempUser");
        assertEquals(bio, tempUser.getBio());
    }

    @WithUserDetails(value = "tempUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void 패스워드_수정_정상() throws Exception {

        mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL)
                    .param("password", "12341234")
                    .param("passwordConfirm", "12341234")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account tempUser = accountRepository.findByNickname("tempUser");
        assertTrue(passwordEncoder.matches("12341234", tempUser.getPassword()));
    }

}