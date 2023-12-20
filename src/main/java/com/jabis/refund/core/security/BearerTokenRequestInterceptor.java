package com.jabis.refund.core.security;

import com.jabis.refund.core.context.RequestContextHolder;
import com.jabis.refund.core.matcher.RequestMatcher;
import com.jabis.refund.core.security.token.BearerTokenRequestMatcher;
import com.jabis.refund.core.security.token.OAuth2TokenConverter;
import com.jabis.refund.core.security.token.bearer.DefaultBearerTokenResolver;
import com.jabis.refund.core.security.token.jwt.Jwt;
import com.jabis.refund.dto.UserProfileDto;
import com.jabis.refund.exception.ExpiredTokenException;
import com.jabis.refund.exception.InvalidBearerTokenException;
import com.jabis.refund.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.text.ParseException;

@Component
public class BearerTokenRequestInterceptor implements HandlerInterceptor {

    private final OAuth2TokenConverter tokenConverter;

    private final UserService userService;

    private final RequestMatcher requestMatcher = new BearerTokenRequestMatcher(new DefaultBearerTokenResolver());

    public BearerTokenRequestInterceptor(OAuth2TokenConverter tokenConverter, UserService userService) {

        this.tokenConverter = tokenConverter;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!requestMatcher.matches(request)) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());

            throw new InvalidBearerTokenException();
        }

        Jwt decodedJwt;
        try {
            decodedJwt = tokenConverter.decode(RequestContextHolder.get().getToken());
        } catch (BadJOSEException | JOSEException | ParseException e) {
            if(e.getMessage().contains("Expire"))
                throw new ExpiredTokenException();
            else
                throw new RuntimeException(e);
        }


        final String subjectUserId = decodedJwt.getSubject();
        final UserProfileDto userProfileDto = userService.getMyProfile(subjectUserId);
        RequestContextHolder.get().setUserId(userProfileDto.getUserId());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestContextHolder.clear();
    }
}
