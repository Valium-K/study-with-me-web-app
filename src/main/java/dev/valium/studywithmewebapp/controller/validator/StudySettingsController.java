package dev.valium.studywithmewebapp.controller.validator;

import dev.valium.studywithmewebapp.controller.dto.form.StudyBannerForm;
import dev.valium.studywithmewebapp.controller.dto.form.StudyDescriptionForm;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import dev.valium.studywithmewebapp.domain.Study;
import dev.valium.studywithmewebapp.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingsController {
    private final String STUDY_SETTINGS_VIEW = "study/settings";

    private final StudyService studyService;

    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getUpdatableStudy(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(StudyDescriptionForm.create(study));

        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path,
                                  @Valid StudyDescriptionForm form, BindingResult result,
                                  Model model, RedirectAttributes attributes) {
        Study study = studyService.getUpdatableStudy(account, path);

        if(result.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);

            return STUDY_SETTINGS_VIEW + "/description";
        }

        studyService.updateStudyDescription(study, form);
        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");

        return "redirect:/study/" + getPath(path) + "/settings/description";
    }

    @GetMapping("/banner")
    public String studyBannerForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getUpdatableStudy(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("studyBannerForm", StudyBannerForm.create(study));

        return STUDY_SETTINGS_VIEW + "/banner";
    }

    @PostMapping("/banner")
    public String updateBanner(@CurrentUser Account account, @PathVariable String path,
                                  @Valid StudyBannerForm form, BindingResult result,
                                  Model model, RedirectAttributes attributes) {
        Study study = studyService.getUpdatableStudy(account, path);

        studyService.updateStudyBanner(study, form);
        attributes.addFlashAttribute("message", "스터디 Banner를 수정했습니다.");

        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableBanner(@CurrentUser Account account, @PathVariable String path,
                                  Model model, RedirectAttributes attributes) {
        Study study = studyService.getUpdatableStudy(account, path);

        studyService.setStudyBanner(study, true);
        attributes.addFlashAttribute("message", "Banner를 사용합니다.");

        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableBanner(@CurrentUser Account account, @PathVariable String path,
                               Model model, RedirectAttributes attributes) {
        Study study = studyService.getUpdatableStudy(account, path);

        studyService.setStudyBanner(study, false);
        attributes.addFlashAttribute("message", "Banner를 사용하지 않습니다.");

        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
