package com.jabis.refund.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jabis.refund.repository.entity.user.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Schema(description = "회원가입 요청 파라미터")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserProfileDto {

    @JsonInclude(Include.NON_NULL)
    @Schema(description = "암호")
    @NotBlank(message = "암호는 필수값입니다.")
    private String password;

    @Schema(description = "사용자 아이디")
    @NotBlank(message = "사용자 아이디는 필수값입니다.")
    private String userId;

    @Schema(description = "이름")
    @NotBlank(message = "이름은 필수값입니다.")
    private String name;

    @Schema(description = "주민등록번호")
    @NotBlank(message = "주민등록번호는 필수값입니다.")
    @Pattern(regexp = "^\\d{6}-[1-4]\\d{6}$")
    private String regNo;

    public UserProfileDto(UserProfile userProfile) {

        this.userId = userProfile.getUserId();
        this.name = userProfile.getName();
        this.regNo = userProfile.getRegNo();
    }
}
