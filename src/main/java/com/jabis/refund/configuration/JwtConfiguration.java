package com.jabis.refund.configuration;

import com.jabis.refund.core.security.token.OAuth2TokenConverter;
import com.jabis.refund.core.security.token.jwt.JwtOAuth2TokenConverter;
import com.jabis.refund.dto.JwtConfigurationProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class JwtConfiguration {

    private final JwtConfigurationProperties jwtConfigurationProperties;
    private int port;

    public JwtConfiguration(JwtConfigurationProperties jwtConfigurationProperties, ServerProperties serverProperties) {

        this.jwtConfigurationProperties = jwtConfigurationProperties;
        this.port = serverProperties.getPort();
    }

    @Bean
    public ConfigurableJWTProcessor<SecurityContext> jwtProcessor() throws MalformedURLException {

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

//      symmetric
//      JWKSource<SecurityContext> keySource = new ImmutableSecret<>(jwtConfigurationProperties.publicKey().getBytes());

//      asymmetric
//        JWKSet jwkSet = JWKSet.parse(jwtConfigurationProperties.publicKey());
//        final JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);

//      jwks
        final URL url = UriComponentsBuilder.newInstance()
                                            .scheme("http")
                                            .host("localhost")
                                            .port(port)
                                            .path("/.well-known/jwks.json")
                                            .build().toUri().toURL();
        JWKSource<SecurityContext> keySource = JWKSourceBuilder.create(url).build();

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        return jwtProcessor;
    }

    @Bean
    public OAuth2TokenConverter jwtAccessTokenConverter() throws MalformedURLException {
        return new JwtOAuth2TokenConverter(jwtProcessor(), jwtConfigurationProperties);
    }
}
