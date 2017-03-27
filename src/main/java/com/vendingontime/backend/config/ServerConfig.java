package com.vendingontime.backend.config;

/**
 * Created by alberto on 27/3/17.
 */
public interface ServerConfig {
    String getString(ServerVariable variable);
    int getInt(ServerVariable variable);
}
