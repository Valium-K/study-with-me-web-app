package dev.valium.studywithmewebapp.repository;

import dev.valium.studywithmewebapp.controller.dto.AccountDto;
import dev.valium.studywithmewebapp.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select new dev.valium.studywithmewebapp.controller.dto.AccountDto(a.nickname, a.email, a.password) " +
            "from Account a where a.email = :email")
    AccountDto findDtoByEmail(@Param("email") String email);

    @Query("select a from Account a join fetch a.topicOfInterests at where a.id = :id")
    Optional<Account> findByIdWithTags(@Param("id") Long id);

    @Transactional
    Account findByEmail(String email);
    Account findReadOnlyByEmail(String email);

    @Query("select new dev.valium.studywithmewebapp.controller.dto.AccountDto(a.nickname, a.email, a.password) " +
            "from Account a where a.nickname = :nickname")
    AccountDto findDtoByNickname(@Param("nickname") String nickname);
    Account findByNickname(String nickname);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
