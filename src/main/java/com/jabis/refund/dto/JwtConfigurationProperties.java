package com.jabis.refund.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtConfigurationProperties(
        int accessExpirationTime,
        String privateKey,
        String publicKey
) {}
