package com.jabis.refund.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "로그인 요청 파라미터")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIdPasswordToken {

    @Schema(description = "사용자 아이디")
    @NotBlank(message = "사용자 아이디는 필수값입니다.")
    private String userId;

    @Schema(description = "암호")
    @NotBlank(message = "암호는 필수값입니다.")
    private String password;
}
