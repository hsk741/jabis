package com.jabis.refund.core.security.token;

import com.jabis.refund.core.security.token.jwt.Jwt;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;

import java.text.ParseException;
import java.time.Instant;

public interface OAuth2TokenConverter {

    OAuth2Token encode(String username);

    String generate(String userId, Instant issuedAt, Instant expiresAt);

    Jwt decode(String token) throws BadJOSEException, JOSEException, ParseException;

    JWT parse(String token) throws ParseException;
}
