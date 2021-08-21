package dev.valium.studywithmewebapp.controller.dto.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AccountForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[a-z|A-Z|ㄱ-ㅎ|가-힣|0-9|_|-]{3,10}$")
    private String nickname;

    public AccountForm() {

    }
}
