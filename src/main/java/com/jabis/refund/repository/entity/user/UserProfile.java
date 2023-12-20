package com.jabis.refund.repository.entity.user;

import com.jabis.refund.dto.UserProfileDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class UserProfile implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String password;

    @Column(unique = true)
    private String userId;

    private String name;

    @Column(unique = true)
    private String regNo;

    @CreatedDate
    private LocalDateTime createdAt;

    public UserProfile(UserProfileDto userProfileDto) {

        this.userId = userProfileDto.getUserId();
        this.password = userProfileDto.getPassword();
        this.name = userProfileDto.getName();
        this.regNo = userProfileDto.getRegNo();
        this.createdAt = LocalDateTime.now();
    }
}
