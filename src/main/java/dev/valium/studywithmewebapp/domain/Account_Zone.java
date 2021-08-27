package dev.valium.studywithmewebapp.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter @Getter
@EqualsAndHashCode(of = {"id"})
@Builder @AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account_Zone {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public static Account_Zone  createAccount_Zone(Account account, Zone zone) {
        return Account_Zone.builder()
                .account(account)
                .zone(zone)
                .build();
    }
}
