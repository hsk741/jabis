package com.jabis.refund.dto.scrap;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "소득공제 응답 메시지")
@Getter
public class DeductionItem {

	@JsonProperty("금액")
	@JsonAlias("총납임금액")	// 소득구분 : 퇴직연금
	private String amount;

	@JsonProperty("소득구분")
	private String incomeClassification;
}
