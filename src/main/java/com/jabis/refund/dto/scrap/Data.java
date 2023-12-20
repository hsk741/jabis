package com.jabis.refund.dto.scrap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "스크래핑 응답 메시지")
@Getter
public class Data {

	@Schema(description = "응답 데이터")
	private JsonList jsonList;

	@Schema(description = "앱 버전")
	private String appVer;

	@Schema(description = "에러 메시지")
	private String errMsg;

	@Schema(description = "기업")
	private String company;

	@Schema(description = "서비스코드")
	private String svcCd;

	@Schema(description = "호스트명")
	private String hostNm;

	@Schema(description = "")
	private String workerResDt;

	@Schema(description = "")
	private String workerReqDt;
}
