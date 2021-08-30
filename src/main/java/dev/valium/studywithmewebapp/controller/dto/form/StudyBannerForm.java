package dev.valium.studywithmewebapp.controller.dto.form;

import dev.valium.studywithmewebapp.domain.Study;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudyBannerForm {

    private boolean useBanner;

    private String image;

    public static StudyBannerForm create(Study study) {
        StudyBannerForm form = new StudyBannerForm();

        form.setUseBanner(study.isUseBanner());
        form.setImage(study.getImage());

        return form;
    }
}
