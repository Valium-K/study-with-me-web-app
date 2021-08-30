package dev.valium.studywithmewebapp.service;

import dev.valium.studywithmewebapp.controller.dto.form.StudyBannerForm;
import dev.valium.studywithmewebapp.controller.dto.form.StudyDescriptionForm;
import dev.valium.studywithmewebapp.domain.Account;
import dev.valium.studywithmewebapp.domain.Study;
import dev.valium.studywithmewebapp.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;


    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);

        newStudy.addManager(account);

        return newStudy;
    }

    public Study getStudy(String path) {
        return Optional.ofNullable(
                studyRepository.findByPath(path)
        ).orElseThrow(
                () -> new IllegalArgumentException(path + "에 해당하는 스터디를 찾을 수 없습니다.")
        );
    }

    public Study getUpdatableStudy(Account account, String path) {
        Study study = this.getStudy(path);

        if(!account.isManagerOf(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다");
        }
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm form) {
        study.setShortDescription(form.getShortDescription());
        study.setFullDescription(form.getFullDescription());
    }

    public void setStudyBanner(Study study, boolean b) {
        study.setUseBanner(b);

        // 이미지 킵해두기
        // study.setImage(null);
    }

    public void updateStudyBanner(Study study, StudyBannerForm form) {
        study.setImage(form.getImage());
        study.setUseBanner(true);
    }
}
