package com.jabis.refund.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결정세액 관련 record")
public record DeterminedTaxAmount(

        @Schema(description = "산출세액")
        double calculatedTaxAmount,

        @Schema(description = "근로소득세액공제금액")
        double employmentIncomeTaxCreditAmount,

        @Schema(description = "특별 및 표준세액공제금액 총합")
        double taxCreditAmount,

        @Schema(description = "퇴직연금세액공제금액")
        double retirementPensionTaxCreditAmount) {

    public double determinedTaxAmount() {
        return Math.max(0, calculatedTaxAmount - employmentIncomeTaxCreditAmount - retirementPensionTaxCreditAmount - taxCreditAmount);
    }
}
