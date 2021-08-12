package dev.valium.studywithmewebapp.controller.dto;

import dev.valium.studywithmewebapp.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

// spring security가 다루는 user정보와 도메인에서 다루는 user 정보의 어댑터
// 즉, Principal 객체가 된다.
@Getter
public class UserAccount extends User {// User 클래스는 spring security의 것임에 유의
    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
