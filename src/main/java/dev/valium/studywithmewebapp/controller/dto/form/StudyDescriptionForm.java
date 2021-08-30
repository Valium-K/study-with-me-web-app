package dev.valium.studywithmewebapp.controller.dto.form;

import dev.valium.studywithmewebapp.domain.Study;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class StudyDescriptionForm {

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

    public static StudyDescriptionForm create(Study study) {
        StudyDescriptionForm form = new StudyDescriptionForm();

        form.setShortDescription(study.getShortDescription());
        form.setFullDescription(study.getFullDescription());

        return form;
    }
}
