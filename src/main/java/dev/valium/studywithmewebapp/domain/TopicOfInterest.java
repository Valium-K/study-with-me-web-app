package dev.valium.studywithmewebapp.domain;

import lombok.*;

import javax.persistence.*;

@Entity(name = "account_tag")
@Setter @Getter @EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicOfInterest {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static TopicOfInterest createTopicOfInterest(Account account, Tag tag) {
        return TopicOfInterest.builder().tag(tag).account(account).build();
    }
}
