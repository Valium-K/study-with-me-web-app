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
    public String newStudySubmit(@CurrentUser Account account,
                                 @Valid StudyForm form, BindingResult result) {
        if(result.hasErrors()) {
            return "study/form";
        }

        if(studyRepository.existsByPath(form.getPath())) {
            result.rejectValue("path", "wrong.path", "스터디 경로값을 사용할 수 없습니다.");
            return "study/form";
        }

        Study study = studyService.createNewStudy(Study.createStudy(form), account);

        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }
}
