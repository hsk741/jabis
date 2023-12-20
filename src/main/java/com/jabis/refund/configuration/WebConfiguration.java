package com.jabis.refund.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final HandlerInterceptor bearerTokenRequestInterceptor;

    public WebConfiguration(HandlerInterceptor bearerTokenRequestInterceptor) {
        this.bearerTokenRequestInterceptor = bearerTokenRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(bearerTokenRequestInterceptor)
                .excludePathPatterns("/swagger-ui.html", "/swagger-ui/**",
                        "/swagger-resources/**", "/v3/api-docs/**",
                        "/webjar/**",
                        "/szs/signup", "/szs/login", "/.well-known/jwks.json");
    }
}
