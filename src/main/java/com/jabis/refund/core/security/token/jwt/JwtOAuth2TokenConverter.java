package com.jabis.refund.core.security.token.jwt;

import com.jabis.refund.core.security.token.DefaultOAuth2Token;
import com.jabis.refund.core.security.token.OAuth2Token;
import com.jabis.refund.core.security.token.OAuth2TokenConverter;
import com.jabis.refund.dto.JwtConfigurationProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class JwtOAuth2TokenConverter implements OAuth2TokenConverter {

    @Value("${spring.application.name}")
    private String applicationName;

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @Override
    public OAuth2Token encode(String username) {

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtConfigurationProperties.accessExpirationTime(), ChronoUnit.SECONDS);

        return new DefaultOAuth2Token(generate(username, issuedAt, expiresAt), issuedAt, expiresAt);
    }

    @Override
    public String generate(String userId, Instant issuedAt, Instant expiresAt) {

        JWTClaimsSet.Builder jwtClaimsBuilder = new JWTClaimsSet.Builder();
        jwtClaimsBuilder
                .issuer(applicationName)    // The value of the 'iss' claim is a String or URL (StringOrURI).
                .subject(userId)            // sub : Principal(userId)
                .audience(Collections.singletonList(userId))
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .notBeforeTime(Date.from(issuedAt))
                .jwtID(UUID.randomUUID().toString());

        JWSHeader.Builder jwsHeaderBuilder = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(applicationName).type(JOSEObjectType.JWT);

        JWSObject jwsObject = new JWSObject(jwsHeaderBuilder.build(),
                new Payload(jwtClaimsBuilder.build().toJSONObject()));

        try {

            JWSSigner signer = new RSASSASigner(RSAKey.parse(jwtConfigurationProperties.privateKey()));
            jwsObject.sign(signer);
        } catch (JOSEException | ParseException e) {

            log.error("jwt signing failure", e);

            throw new RuntimeException(e);
        }

        return jwsObject.serialize();
    }

    @Override
    public Jwt decode(String token) throws BadJOSEException, JOSEException, ParseException {

        JWT parsedJwt = parse(token);

        final JWTClaimsSet claimsSet = this.jwtProcessor.process(parsedJwt, null);

        Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
        Map<String, Object> claims = claimsSet.getClaims();

        return new Jwt(token, claimsSet.getIssueTime().toInstant(), claimsSet.getExpirationTime().toInstant(), headers, claims);
    }

    @Override
    public JWT parse(String token) throws ParseException {
        return JWTParser.parse(token);
    }
}
