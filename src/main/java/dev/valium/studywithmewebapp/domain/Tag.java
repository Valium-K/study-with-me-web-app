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


    public static Tag createTag(String tagName) {
        return Tag.builder().title(tagName).build();
    }
    // 단방향
    // @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    // private Set<TopicOfInterest> accountTags;
}
