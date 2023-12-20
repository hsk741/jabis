package com.jabis.refund.core.security.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.Instant;

@Getter
@EqualsAndHashCode
public class DefaultOAuth2Token implements Serializable, OAuth2Token {

	private final String tokenValue;

	private final Instant issuedAt;

	private final Instant expiresAt;

	public DefaultOAuth2Token(String tokenValue) {
		this(tokenValue, null, null);
	}

	public DefaultOAuth2Token(String tokenValue, @Nullable Instant issuedAt, @Nullable Instant expiresAt) {

		Assert.hasText(tokenValue, "tokenValue cannot be empty");

		if (issuedAt != null && expiresAt != null) {
			Assert.isTrue(expiresAt.isAfter(issuedAt), "expiresAt must be after issuedAt");
		}

		this.tokenValue = tokenValue;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}
}
