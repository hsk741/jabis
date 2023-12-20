package com.jabis.refund.repository;

import com.jabis.refund.repository.entity.scrap.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    @Query("select d from Deduction d join fetch d.salaries s where d.userId = :userId")
    Optional<Deduction> findByUserId(String userId);

    void deleteByUserId(String userId);
}
