package com.jabis.refund.dto.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "개별급여 응답 메시지")
@Getter
public class SalaryItem{

	@JsonProperty("소득내역")
	private String incomeDetails;

	@JsonProperty("총지급액")
	private String totalPaidAmount;

	@JsonProperty("업무시작일")
	private String bizStartDate;

	@JsonProperty("기업명")
	private String companyName;

	@JsonProperty("이름")
	private String name;

	@JsonProperty("지급일")
	private String paymentDate;

	@JsonProperty("업무종료일")
	private String bizEndDate;

	@JsonProperty("주민등록번호")
	private String residentRegistrationNumber;

	@JsonProperty("소득구분")
	private String incomeClassification;

	@JsonProperty("사업자등록번호")
	private String companyRegistrationNumber;
}
