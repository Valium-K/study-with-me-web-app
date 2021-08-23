package dev.valium.studywithmewebapp.repository;

import dev.valium.studywithmewebapp.domain.TopicOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface TopicOfInterestRepository extends JpaRepository<TopicOfInterest, Long> {

    @Query("select at from account_tag at join fetch at.tag t where at.account.id = :id")
    Set<TopicOfInterest> findTopicOfInterestsByAccount_Id(@Param("id") Long id);
}
