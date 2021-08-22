package dev.valium.studywithmewebapp.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Tag {

    @Id @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    // 단방향
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private Set<Account2Tag> account2Tags;
}
