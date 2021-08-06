package dev.valium.studywithmewebapp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @DisplayName("회원가입 화면이 보이는지")
    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))                            // get sign-up
                //.andDo(print())                                             // 응답 print
                .andExpect(status().isOk())                                   // 200을 기대
                .andExpect(view().name("account/sign-up"));    // 그리고 view가 보여야한다.
    }

}