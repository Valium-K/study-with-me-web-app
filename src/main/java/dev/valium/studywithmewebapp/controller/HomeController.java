package dev.valium.studywithmewebapp.controller;

import dev.valium.studywithmewebapp.domain.Account;
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

    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
    protected @interface CurrentUser {
        // spring boot의 기능인 에노테이션 상속(순수 java는 안됨)을 이용해 애노테이션 길이를 줄였다.
        // 이 애노테이션을 참조하는 현재 객체가 anonymousUser면 null 아니면 account 설정
        // account라는 것은 UserAccount의 필드객체 명으로, 서로 이름이 같아야한다.
    }


}
