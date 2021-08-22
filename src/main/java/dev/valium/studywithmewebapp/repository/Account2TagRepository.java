package dev.valium.studywithmewebapp.repository;

import dev.valium.studywithmewebapp.domain.Account2Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
public interface Account2TagRepository extends JpaRepository<Account2Tag, Long> {

    @Query("select at from account_tag at where at.account.id = :id")
    Optional<Account2Tag> findTag(@Param("id") Long id);
}
