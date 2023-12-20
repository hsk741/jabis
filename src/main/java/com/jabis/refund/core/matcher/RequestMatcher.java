package com.jabis.refund.core.matcher;


import jakarta.servlet.http.HttpServletRequest;

public interface RequestMatcher {
    boolean matches(HttpServletRequest request);
}
