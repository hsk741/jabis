package com.jabis.refund;

import com.jabis.refund.core.security.cipher.CryptographyOperations;
import com.jabis.refund.repository.EnableUserProfileRepository;
import com.jabis.refund.repository.entity.user.EnableUserProfile;
import com.jabis.refund.support.csv.CsvProcessor;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class JabisApplication {

    public static void main(String[] args) {
        SpringApplication.run(JabisApplication.class, args);
    }

    @Bean
    public OpenAPI openAPI(@Value("${spring.profiles.active}") String active) {

        Info info = new Info().title("SZS Refund Open API - " + active)
                              .version("v1")
                              .description("고객에게 refund기능을 제공하는 OPEN API입니다.");

        List<Server> servers = Collections.singletonList(new Server().url("http://localhost:8080")
                                                                     .description("Refund API (" + active + ")"));

        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                                            .scheme("bearer").bearerFormat("JWT")
                                                            .in(SecurityScheme.In.HEADER).name("Authorization");

        return new OpenAPI().components(new Components().addSecuritySchemes("Authorization", securityScheme))
                            .info(info)
                            .servers(servers);
    }

    @Component
    @RequiredArgsConstructor
    static class InitialEnabledUserLoader {

        private final CsvProcessor csvProcessor;

        private final EnableUserProfileRepository enableUserProfileRepository;

        private final CryptographyOperations cryptographyOperations;

        @PostConstruct
        public void loadInitialData() throws IOException {

            final ClassPathResource classPathResource = new ClassPathResource("enable-user.csv");
            csvProcessor.processCsv(classPathResource.getInputStream(), csvDefinitions ->
                    csvDefinitions.contents()
                                  .stream()
                                  .map(records -> new EnableUserProfile(records[0],
                                                                        cryptographyOperations.encrypt(records[1])))
                                  .forEach(enableUserProfileRepository::save));
        }
    }
}
