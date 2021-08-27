package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import org.dom4j.rule.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController {

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Mode mode) {

    }
}
