package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.settings.Password;
import dev.valium.studywithmewebapp.controller.dto.settings.Profile;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import dev.valium.studywithmewebapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.PreUpdate;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    private final AccountService accountService;

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);     // TODO AccountSettingsDTO 만들기
        model.addAttribute(new Profile(account));

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile,
                                BindingResult result, Model model, RedirectAttributes attributes) {
        if(result.hasErrors()) {
            model.addAttribute(account);

            return SETTINGS_PROFILE_VIEW_NAME;
        }

        // 로그인된 accunt정보와 profile정보를 합하여 update해라
        accountService.updateProfile(account, profile);

        // spring mvc - redirect 후 한번만 쓸 일회용 데이터를 전성
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Password());

        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid Password password,
                                 BindingResult result, Model model, RedirectAttributes attributes) {

        if(result.hasErrors()) {
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        if(password != null && !password.getPassword().equals(password.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "invalid.passwordConfirm", "비밀번호 확인이 같지 않습니다.");

            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        attributes.addFlashAttribute("message", "비밀번호를 수정하였습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }
}
