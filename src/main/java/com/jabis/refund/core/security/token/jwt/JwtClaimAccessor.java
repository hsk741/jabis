package com.jabis.refund.core.security.token.jwt;

import com.nimbusds.jwt.JWTClaimNames;

import java.net.URL;
import java.time.Instant;
import java.util.List;

public interface JwtClaimAccessor extends ClaimAccessor {

	default URL getIssuer() {
		return this.getClaimAsURL(JWTClaimNames.ISSUER);
	}

	default String getSubject() {
		return this.getClaimAsString(JWTClaimNames.SUBJECT);
	}

	default List<String> getAudience() {
		return this.getClaimAsStringList(JWTClaimNames.AUDIENCE);
	}

	default Instant getExpiresAt() {
		return this.getClaimAsInstant(JWTClaimNames.EXPIRATION_TIME);
	}

	default Instant getNotBefore() {
		return this.getClaimAsInstant(JWTClaimNames.NOT_BEFORE);
	}

	default Instant getIssuedAt() {
		return this.getClaimAsInstant(JWTClaimNames.ISSUED_AT);
	}

	default String getId() {
		return this.getClaimAsString(JWTClaimNames.JWT_ID);
	}
}
