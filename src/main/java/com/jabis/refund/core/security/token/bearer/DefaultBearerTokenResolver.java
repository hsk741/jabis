package com.jabis.refund.core.security.token.bearer;

import com.jabis.refund.core.context.RequestContextHolder;
import com.jabis.refund.core.security.token.OAuth2Token;
import com.jabis.refund.exception.InvalidBearerTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DefaultBearerTokenResolver implements BearerTokenResolver {

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*)$");

    @Override
    public String resolve(HttpServletRequest request) {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.startsWithIgnoreCase(authorization, OAuth2Token.BEARER_TYPE.toLowerCase())) {

            Matcher matcher = authorizationPattern.matcher(authorization);

            if (!matcher.matches()) {
                throw new InvalidBearerTokenException();
            }

            String token = matcher.group("token");

            RequestContextHolder.get().setToken(token);

            return token;
        }

        return null;
    }
}
