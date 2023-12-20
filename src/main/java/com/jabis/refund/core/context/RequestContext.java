package com.jabis.refund.core.context;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {

    private String token;

    private String userId;

    private final Map<String, Object> namedParameter = new HashMap<String, Object>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public RequestContext addNamedParameter(String key, Object value) {

        this.namedParameter.put(key, value);

        return this;
    }

    public Object getNamedParameter(String key) {
        return this.namedParameter.get(key);
    }
}
