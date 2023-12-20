package com.jabis.refund.repository;

import com.jabis.refund.repository.entity.user.EnableUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnableUserProfileRepository extends JpaRepository<EnableUserProfile, Long> {
    Optional<EnableUserProfile> findByNameAndRegNo(String name, String encryptedRegNo);
}
