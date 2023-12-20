package com.jabis.refund.dto.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "jsonList 응답 메시지")
@Getter
public class JsonList {

	@JsonProperty("급여")
	private List<SalaryItem> salaryItems;

	@JsonProperty("산출세액")
	private String caculatedTaxAmount;

	@JsonProperty("소득공제")
	private List<DeductionItem> deductionItems;
}
