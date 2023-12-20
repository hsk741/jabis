package com.jabis.refund.core.security.token;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "로그인 응답 토큰")
public interface OAuth2Token {

	String BEARER_TYPE = "Bearer";

	@Schema(description = "JWT access token")
	String getTokenValue();

	@Schema(description = "토큰 발행 일시")
	default Instant getIssuedAt() {
		return null;
	}

	@Schema(description = "토큰 만료 일시")
	default Instant getExpiresAt() {
		return null;
	}
}
