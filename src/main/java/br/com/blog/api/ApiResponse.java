package br.com.blog.api;

import java.util.LinkedHashMap;

public class ApiResponse extends LinkedHashMap {

    public ApiResponse status(int status) {
        this.put("status", status);
        return this;
    }

    public ApiResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public ApiResponse data(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
