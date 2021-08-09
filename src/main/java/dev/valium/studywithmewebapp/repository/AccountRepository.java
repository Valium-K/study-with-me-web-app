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
    AccountDto findByEmail(@Param("email") String email);

    @Query("select new dev.valium.studywithmewebapp.controller.dto.AccountDto(a.nickname, a.email, a.password) " +
            "from Account a where a.nickname = :nickname")
    AccountDto findByNickname(@Param("nickname") String nickname);


}
