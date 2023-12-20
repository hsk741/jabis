package com.jabis.refund.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스크래핑한 정보의 결정세액 및 퇴직연금세액공제 응답 메시지")
public record ScrapRefundResponse(
        @JsonProperty("이름")
        String name,
        @JsonProperty("결정세액")
        double determinedTaxAmount,
        @JsonProperty("퇴직연금세액공제")
        double retirementPensionTaxCreditAmount) {
}
