package com.jabis.refund.api;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

@Slf4j
@Disabled
public class RSAKeyTest {

    @Test
    public void keyTest() throws JOSEException, ParseException {
        RSAKey jwk = new RSAKeyGenerator(2048).keyID("szs-application").generate();
        String rsaJwk = jwk.toJSONString();                     // Private Key
        String rsaPublicJwk = jwk.toPublicJWK().toJSONString(); // Public Key

        final RSAKey privateRsaKey = RSAKey.parse(rsaJwk);
        final RSAKey publicRsaKey = RSAKey.parse(rsaPublicJwk);

        log.info("private key generated : {}", rsaJwk);
        log.info("public key generated : {}", rsaPublicJwk);
    }

    @Test
    public void readFileContents() {

        final ClassPathResource classPathResource = new ClassPathResource("jwks.json");
        try {
            final Path path = Paths.get(classPathResource.getURI());
            final String fileContents = Files.readString(path);
            log.info("file contents : {}", fileContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
