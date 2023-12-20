package com.jabis.refund.core.security.token.jwt;

import com.jabis.refund.core.security.token.DefaultOAuth2Token;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Jwt extends DefaultOAuth2Token implements JwtClaimAccessor {

    private final Map<String, Object> headers;

    private final Map<String, Object> claims;

    public Jwt(String tokenValue, Map<String, Object> headers, Map<String, Object> claims) {

        super(tokenValue);

        Assert.notEmpty(headers, "headers cannot be empty");
        Assert.notEmpty(claims, "claims cannot be empty");
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.claims = Collections.unmodifiableMap(new LinkedHashMap<>(claims));
    }

    public Jwt(String tokenValue, Instant issuedAt, Instant expiresAt, Map<String, Object> headers,
               Map<String, Object> claims) {

        super(tokenValue, issuedAt, expiresAt);

        Assert.notEmpty(headers, "headers cannot be empty");
        Assert.notEmpty(claims, "claims cannot be empty");
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.claims = Collections.unmodifiableMap(new LinkedHashMap<>(claims));
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    public Map<String, Object> getClaims() {
        return this.claims;
    }
}
