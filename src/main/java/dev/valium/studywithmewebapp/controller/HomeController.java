package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.annotation.*;

@Controller
public class HomeController {

    @GetMapping
    public String home(@CurrentUser Account account, Model model) {
        // 아래코드는 model.addAttribute(account)와 같지만, account=null 인 객체를 명시하기위해 이대로 씀
        model.addAttribute("account", (account != null) ? account : null);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
