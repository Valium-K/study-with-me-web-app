package dev.valium.studywithmewebapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.valium.studywithmewebapp.controller.dto.form.TagForm;
import dev.valium.studywithmewebapp.controller.dto.form.ZoneForm;
import dev.valium.studywithmewebapp.controller.dto.settings.AccountForm;
import dev.valium.studywithmewebapp.controller.dto.settings.Notifications;
import dev.valium.studywithmewebapp.controller.dto.settings.Password;
import dev.valium.studywithmewebapp.controller.dto.settings.Profile;
import dev.valium.studywithmewebapp.domain.*;
import dev.valium.studywithmewebapp.repository.Account_ZoneRepository;
import dev.valium.studywithmewebapp.repository.TagRepository;
import dev.valium.studywithmewebapp.repository.TopicOfInterestRepository;
import dev.valium.studywithmewebapp.repository.ZoneRepository;
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
    static final String SETTINGS_ZONE_VIEW_NAME = "settings/zones";
    static final String SETTINGS_ZONE_URL = "/settings/zones";


    private final AccountService accountService;
    private final TagRepository tagRepository;
    private final TopicOfInterestRepository topicOfInterestRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;
    private final Account_ZoneRepository account_zoneRepository;

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);     // TODO AccountSettingsDTO ?????????
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

        // ???????????? accunt????????? profile????????? ????????? update??????
        accountService.updateProfile(account, profile);

        // spring mvc - redirect ??? ????????? ??? ????????? ???????????? ??????
        attributes.addFlashAttribute("message", "???????????? ?????????????????????.");
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
                    , "???????????? ????????? ?????? ????????????.");

            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(password, account);

        attributes.addFlashAttribute("message", "??????????????? ?????????????????????.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationUpdateForm(@CurrentUser Account account, Model model) {
        Account foundAccount = accountService.getAccount(account.getId());
        model.addAttribute("account", foundAccount);
        model.addAttribute(new Notifications(foundAccount));

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

        attributes.addFlashAttribute("message", "????????? ?????????????????????.");
        model.addAttribute(notifications);

        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String accountUpdateForm(@CurrentUser Account account, Model model) {
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
                    , "?????? ???????????? ???????????????.");

            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(accountForm, account);

        attributes.addFlashAttribute("message", "???????????? ?????????????????????.");
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
                .orElseThrow(Exception::new);

        accountService.removeTopicOfInterest(account, foundToi);
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_ZONE_URL)
    public String updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Account_Zone> account_zones = accountService.getAccount_Zones(account);
        model.addAttribute("zones", account_zones.stream().map(az -> az.getZone().toString()).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return SETTINGS_ZONE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ZONE_URL + "/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_ZONE_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) throws Exception {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }


        Account_Zone account_zone = account_zoneRepository
                .findByZone(zone)
                .orElseThrow(Exception::new);

        accountService.removeZone(account, account_zone);
        return ResponseEntity.ok().build();
    }
}
