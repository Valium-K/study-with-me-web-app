package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.CurrentUser;
import dev.valium.studywithmewebapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.annotation.*;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AccountRepository accountRepository;

    @GetMapping
    public String home(@CurrentUser Account account, Model model) {
        // 아래코드는 model.addAttribute(account)와 같지만, account=null 인 객체를 명시하기위해 이대로 씀
        // model.addAttribute("account", (account != null) ? account : null);
        if(account == null) {
            model.addAttribute("account", null);
        } else {
            Optional<Account> acc = accountRepository.findById(account.getId());
            model.addAttribute("account", acc.get());
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        // TODO password, email 총 쿼리 2번 - 최적화 필요
        return "login";
    }
}
