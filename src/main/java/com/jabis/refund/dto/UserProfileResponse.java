package com.jabis.refund.dto;

import com.jabis.refund.repository.entity.user.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "회원정보 응답 파라미터")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserProfileResponse {

    @Schema(description = "사용자 아이디")
    private String userId;

    @Schema(description = "이름")
    private String name;

    public UserProfileResponse(UserProfile userProfile) {

        this.userId = userProfile.getUserId();
        this.name = userProfile.getName();
    }
}
