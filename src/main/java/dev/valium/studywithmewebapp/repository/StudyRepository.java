package dev.valium.studywithmewebapp.repository;

import dev.valium.studywithmewebapp.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    Study findByPath(String path);

    Study findStudyWithMembersByPath(String path);

    Study findStudyById(Long id);
}
