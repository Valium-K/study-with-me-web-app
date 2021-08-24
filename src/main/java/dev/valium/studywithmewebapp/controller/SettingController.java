package dev.valium.studywithmewebapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.valium.studywithmewebapp.controller.dto.form.TagForm;
import dev.valium.studywithmewebapp.controller.dto.settings.AccountForm;
import dev.valium.studywithmewebapp.controller.dto.settings.Notifications;
import dev.valium.studywithmewebapp.controller.dto.settings.Password;
import dev.valium.studywithmewebapp.controller.dto.settings.Profile;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import dev.valium.studywithmewebapp.domain.Tag;
import dev.valium.studywithmewebapp.domain.TopicOfInterest;
import dev.valium.studywithmewebapp.repository.TagRepository;
import dev.valium.studywithmewebapp.repository.TopicOfInterestRepository;
import dev.valium.studywithmewebapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingController {


    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";
    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATION_URL = "/settings/notifications";
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/settings/account";

    static final String SETTINGS_TAG_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAG_URL = "/settings/tags";


    private final AccountService accountService;
    private final TagRepository tagRepository;
    private final TopicOfInterestRepository topicOfInterestRepository;
    private final ObjectMapper objectMapper;

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
    public String updatePassword(@CurrentUser Account account, Model model,
                                 @Valid Password password, BindingResult result,
                                 RedirectAttributes attributes) {

        if(result.hasErrors()) {
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        if(!password.getPassword().equals(password.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "invalid.passwordConfirm"
                    , "비밀번호 확인이 같지 않습니다.");

            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(password, account);

        attributes.addFlashAttribute("message", "비밀번호를 수정하였습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Notifications(account));

        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String updateNotification(@CurrentUser Account account, Model model,
                                 @Valid Notifications notifications, BindingResult result,
                                 RedirectAttributes attributes) {

        if(result.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATION_VIEW_NAME;
        }

        accountService.updateNotification(notifications, account);

        attributes.addFlashAttribute("message", "비밀번호를 수정하였습니다.");
        model.addAttribute(notifications);

        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String accountUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new AccountForm());

        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, Model model,
                                     @Valid AccountForm accountForm, BindingResult result,
                                     RedirectAttributes attributes) {

        if(result.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        if(accountForm.getNickname().equals(account.getNickname())) {
            result.rejectValue("nickname", "duplicated.nickname"
                    , "이전 닉네임과 동일합니다.");

            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(accountForm, account);

        attributes.addFlashAttribute("message", "닉네임을 수정하였습니다.");
        model.addAttribute(accountForm);

        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @GetMapping(SETTINGS_TAG_URL)
    public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<TopicOfInterest> tois = topicOfInterestRepository.findTopicOfInterestsByAccount_Id(account.getId());
        List<String> InterestingTags = tois.stream().map(toi -> toi.getTag().getTitle()).collect(Collectors.toList());

        model.addAttribute("tags", InterestingTags);
        model.addAttribute(account);

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return SETTINGS_TAG_VIEW_NAME;
    }

    @PostMapping(SETTINGS_TAG_URL + "/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title).orElseGet(() ->
                tagRepository.save(Tag.createTag(title))
        );

        accountService.addTopicOfInterest(account, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_TAG_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) throws Exception {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title).orElse(null);

        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }

        TopicOfInterest foundToi = topicOfInterestRepository
                .findTopicOfInterestByTag(tag)
                .orElseThrow(Exception::new);;

        accountService.removeTopicOfInterest(account, foundToi);
        return ResponseEntity.ok().build();
    }
}
