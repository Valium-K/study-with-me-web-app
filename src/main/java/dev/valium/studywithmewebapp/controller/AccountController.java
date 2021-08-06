package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.controller.dto.signUpForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new signUpForm());
        return "account/sign-up";
    }
}
