package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.form.StudyForm;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import dev.valium.studywithmewebapp.domain.Study;
import dev.valium.studywithmewebapp.repository.StudyRepository;
import dev.valium.studywithmewebapp.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final StudyRepository studyRepository;

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model) {
        model.addAttribute(new StudyForm());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudySubmit(@CurrentUser Account account, Model model,
                                 @Valid StudyForm form, BindingResult result) {
        if(result.hasErrors()) {
            model.addAttribute(account);
            return "study/form";
        }

        if(studyRepository.existsByPath(form.getPath())) {
            result.rejectValue("path", "wrong.path", "스터디 경로값을 사용할 수 없습니다.");
            return "study/form";
        }

        Study study = studyService.createNewStudy(Study.createStudy(form), account);

        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);

        model.addAttribute(account);
        model.addAttribute(study);

        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String viewStudyMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/members";
    }

//    @GetMapping("/study/{path}/join")
//    public String joinStudy(@CurrentUser Account account, @PathVariable String path) {
//        Study study = studyRepository.findStudyWithMembersByPath(path);
//        studyService.addMember(study, account);
//        return "redirect:/study/" + study.getEncodedPath() + "/members";
//    }
//
//    @GetMapping("/study/{path}/leave")
//    public String leaveStudy(@CurrentAccount Account account, @PathVariable String path) {
//        Study study = studyRepository.findStudyWithMembersByPath(path);
//        studyService.removeMember(study, account);
//        return "redirect:/study/" + study.getEncodedPath() + "/members";
//    }
}
