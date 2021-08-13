package dev.valium.studywithmewebapp.domain;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
    // spring boot의 기능인 에노테이션 상속(순수 java는 안됨)을 이용해 애노테이션 길이를 줄였다.
    // 이 애노테이션을 참조하는 현재 객체가 anonymousUser면 null 아니면 account 설정
    // account라는 것은 UserAccount의 필드객체 명으로, 서로 이름이 같아야한다.
}