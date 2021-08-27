package dev.valium.studywithmewebapp.repository;

import dev.valium.studywithmewebapp.domain.Account_Zone;
import dev.valium.studywithmewebapp.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface Account_ZoneRepository extends JpaRepository<Account_Zone, Long> {

    Set<Account_Zone> findAllByAccountId(Long id);

    Optional<Account_Zone> findByZone(Zone zone);
}
