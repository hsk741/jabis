package com.jabis.refund.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class JwksController {

    @GetMapping("/.well-known/jwks.json")
    public String getPublickeyByJWKS() {

        final ClassPathResource classPathResource = new ClassPathResource("jwks.json");
        final String publicKey;

        try {
            final Path path = Paths.get(classPathResource.getURI());
            publicKey = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publicKey;
    }
}
