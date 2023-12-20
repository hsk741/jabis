package com.jabis.refund.core.security.token;

import com.jabis.refund.core.matcher.RequestMatcher;
import com.jabis.refund.core.security.token.bearer.BearerTokenResolver;
import com.jabis.refund.exception.InvalidBearerTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

public final class BearerTokenRequestMatcher implements RequestMatcher {

    private final BearerTokenResolver bearerTokenResolver;

    public BearerTokenRequestMatcher(BearerTokenResolver bearerTokenResolver) {

        Assert.notNull(bearerTokenResolver, "bearerTokenResolver cannot be null");
        this.bearerTokenResolver = bearerTokenResolver;
    }

    @Override
    public boolean matches(HttpServletRequest request) {

        try {
            return this.bearerTokenResolver.resolve(request) != null;
        } catch (InvalidBearerTokenException e) {
            return false;
        }
    }
}
