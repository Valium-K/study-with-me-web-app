package dev.valium.studywithmewebapp.domain;

import dev.valium.studywithmewebapp.controller.dto.form.StudyForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter @Getter
@EqualsAndHashCode(of = {"id"})
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String  shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDataTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public static Study createStudy(StudyForm form) {
        Study study = new Study();
        study.setPath(form.getPath());
        study.setTitle(form.getTitle());
        study.setShortDescription(form.getShortDescription());
        study.setFullDescription(form.getFullDescription());

        return study;
    }

    public void addManager(Account account) {
        this.managers.add(account);
    }
}
