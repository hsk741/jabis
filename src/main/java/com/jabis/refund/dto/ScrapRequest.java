package com.jabis.refund.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "스크래핑 요청 파라미터")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapRequest {

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "주민등록번호")
    private String regNo;
}
