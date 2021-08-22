package dev.valium.studywithmewebapp.domain;

import dev.valium.studywithmewebapp.controller.dto.settings.Notifications;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = {"id"})
@Builder @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    // 유저 - 이메일 / 아이디 로그인 가능
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String nickname;
    private String password;

    // 이메일 인증
    private boolean emailVerified;
    private String emailCheckToken;     // email 검색용 토큰     // TODO 한 번 쓰는 UUID를 Account 테이블에 넣어야 할까??? 고민해보기.
    private LocalDateTime joinedAt;

    // 프로필
    private String bio;
    private String url;
    private String occupation;
    private String location;
    @Lob @Basic(fetch = FetchType.EAGER) // 유저검색시 높은 확률로 같이 쓸 것이다.
    private String profileImage;

    // 알림 설정
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Account2Tag> account2Tags = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();    // TODO 한 번 쓰는 UUID를 Account 테이블에 넣어야 할까??? 고민해보기.
    }

    public void updateNotification(Notifications notifications) {
        this.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
        this.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
        this.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
        this.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
        this.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
        this.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
    }
}
