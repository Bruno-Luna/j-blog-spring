package br.com.blog.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiResult extends LinkedHashMap {

    public ApiResult status(int status) {
        this.put("status", status);
        return this;
    }

    public ApiResult message(String message) {
        this.put("message", message);
        return this;
    }

    public ApiResult data(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public ApiResult errors(Map<String, String> errors) {
        this.put("errors", errors);
        return this;
    }
}
