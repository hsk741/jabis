package com.jabis.refund.dto.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "스크래핑 응답 최상위 파라미터")
@Getter
public class ScrapResponse {

	@JsonProperty("data")
	private Data data;

	@JsonProperty("errors")
	private Errors errors;

	@JsonProperty("status")
	private String status;
}
