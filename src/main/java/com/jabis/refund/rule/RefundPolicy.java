package com.jabis.refund.rule;

import com.jabis.refund.dto.DeterminedTaxAmount;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RefundPolicy {

    private static final double TAX_CREDIT_BASE_AMOUNT = 130000;    // 세액공제 기준금액

    // 근로소득세액공제금액 = 산출세액 * 0.55
    private static double calculateEmploymentIncomeTaxCreditAmount(double calculatedTaxAmount) {
        return calculatedTaxAmount * 0.55;
    }

    // 보험료공제금액 = 보험료납입금액 * 12%
    private static double calculateInsuranceFeeDeductionAmount(double insuranceFee) {
        return Math.max(0, insuranceFee * 0.12);
    }

    // 의료비공제금액 = (의료비납입금액 - 총급여 * 3%) * 15%
    // 의료비공제금액 < 0 => 0원
    private static double calculateMedicalExpensesDeductionAmount(double totalPaidAmount, double medicalExpenses) {
        return Math.max(0, (medicalExpenses - totalPaidAmount * 0.03) * 0.15);
    }

    // 교육비공제금액 = 교육비납입금액 * 15%
    private static double calculateEducationExpensesDeductionAmount(double educationExpenses) {
        return Math.max(0, educationExpenses * 0.15);
    }

    // 기부금공제금액 = 기부금납입금액 * 15%
    private static double calculateDonationDeductionAmount(double donation) {
        return Math.max(0, donation * 0.15);
    }

    // 특별세액공제금액 + 표준세액공제금액
    // 1. 특별세액공제금액의 합이 13만원 미만인 경우 표준세액공제금액이 13만원이고 특별세액공제금액은 0원
    // 2. 특별세액공제금액의 합이 13만원 이상인 경우 표준세액공제금액이 0원
    private static double calculateTaxCreditAmount(double totalPaidAmount, double insuranceFee, double medicalExpenses, double educationExpenses, double donation) {

        // 특별세액공제금액
        double specialTaxCreditAmount = calculateInsuranceFeeDeductionAmount(insuranceFee) + calculateMedicalExpensesDeductionAmount(totalPaidAmount, medicalExpenses) + calculateEducationExpensesDeductionAmount(educationExpenses) + calculateDonationDeductionAmount(donation);

        // 특별세액공제금액과 표준세액공제금액을 비교하여 작은금액을 0으로 초기화
        return Math.max(specialTaxCreditAmount, TAX_CREDIT_BASE_AMOUNT);
    }

    // 퇴직연금세액공제금액 = 퇴직연금납입금액 * 0.15
    public static double getRetirementPensionTaxCreditAmount(Double retirementPension) {
        return retirementPension * 0.15;
    }

    // 결정세액 = 산출세액 - 근로소득세액공제금액 - 퇴직연금세액공제금액 - 특별(일반)세액공제금액
    public static DeterminedTaxAmount getDeterminedTaxAmount(double calculatedTaxAmount,
                                                             double totalPaidAmount,
                                                             double retirementPension,
                                                             double insuranceFee,
                                                             double medicalExpenses,
                                                             double educationExpenses,
                                                             double donation) {

        final double employmentIncomeTaxCreditAmount = calculateEmploymentIncomeTaxCreditAmount(calculatedTaxAmount);
        final double taxCreditAmount = calculateTaxCreditAmount(totalPaidAmount,
                                                                insuranceFee,
                                                                medicalExpenses,
                                                                educationExpenses,
                                                                donation);
        final double retirementPensionTaxCreditAmount = getRetirementPensionTaxCreditAmount(retirementPension);

        return new DeterminedTaxAmount(calculatedTaxAmount, employmentIncomeTaxCreditAmount, taxCreditAmount, retirementPensionTaxCreditAmount);
    }
}
