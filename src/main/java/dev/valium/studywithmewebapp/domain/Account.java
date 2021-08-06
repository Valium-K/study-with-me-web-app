package dev.valium.studywithmewebapp.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = {"id"})
@Builder @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id @GeneratedValue
    private Long id;

    // 유저 - 이메일 / 아이디 로그인 가능
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String nickname;
    private String password;

    // 이메일 인증
    private boolean emailVerified;
    private String emailCheckToken;     // email 검색용 토큰
    private LocalDateTime joinedAt;

    // 프로필
    private String bio;
    private String url;
    private String occupation;
    private String location;
    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    // 알림 설정
    private boolean meetCreatedByEmail;
    private boolean meetCreatedByWeb;
    private boolean meetEnrollmentResultByWeb;
    private boolean meetUpdatedByEmail;
    private boolean meetUpdatedByWeb;
}
