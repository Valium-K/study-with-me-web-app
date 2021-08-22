package dev.valium.studywithmewebapp.controller.dto;

import dev.valium.studywithmewebapp.controller.dto.form.SignUpForm;
import lombok.Data;

@Data
public class AccountDto {
    private String nickname;
    private String email;
    private String password;

    public AccountDto(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public static class SignUpForm2AccountDto {
        public static AccountDto convert(SignUpForm signUpForm) {
            return new AccountDto(signUpForm.getNickname(), signUpForm.getEmail(), signUpForm.getPassword());
        }
    }
}
