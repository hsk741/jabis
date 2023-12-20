package com.jabis.refund.repository.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class EnableUserProfile implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String regNo;

    private LocalDateTime createdAt;

    public EnableUserProfile(String name, String regNo) {

        this.name = name;
        this.regNo = regNo;
        this.createdAt = LocalDateTime.now();
    }
}
