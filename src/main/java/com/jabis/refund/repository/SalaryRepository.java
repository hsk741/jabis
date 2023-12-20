package com.jabis.refund.repository;

import com.jabis.refund.repository.entity.scrap.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    void deleteByUserId(String userId);
}
