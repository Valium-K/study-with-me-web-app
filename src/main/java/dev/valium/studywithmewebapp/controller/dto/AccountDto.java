package dev.valium.studywithmewebapp.controller.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
