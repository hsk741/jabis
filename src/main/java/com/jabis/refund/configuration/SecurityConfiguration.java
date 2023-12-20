package com.jabis.refund.configuration;

import com.jabis.refund.core.security.cipher.AES256Operations;
import com.jabis.refund.core.security.cipher.BcryptHashOperations;
import com.jabis.refund.core.security.cipher.CryptographyOperations;
import com.jabis.refund.core.security.cipher.OneWayPasswordHashOperations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfiguration {

    @Bean
    public CryptographyOperations cryptoOperator() {
        return new AES256Operations();
    }

    @Bean
    public OneWayPasswordHashOperations hashOperator() {
        return new BcryptHashOperations();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
