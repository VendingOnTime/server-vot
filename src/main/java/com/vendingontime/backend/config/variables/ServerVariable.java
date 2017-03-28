package com.vendingontime.backend.config.variables;

/**
 * Created by alberto on 27/3/17.
 */
public enum ServerVariable {
    PORT("8080"),
    ENV(Environment.DEVELOPMENT.toString()),
    DATA_SOURCE("derby_in_memory");

    private final String defaultValue;

    ServerVariable(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
