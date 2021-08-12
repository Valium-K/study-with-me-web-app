package dev.valium.studywithmewebapp.controller.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class EmailVerificationForm {

    @Email
    private String email;

    private String token;
}
