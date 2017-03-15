package com.vendingontime.backend.routes.utils;

/**
 * Created by alberto on 15/3/17.
 */
public interface ResultFactory {
    Object ok(Object data);
    Object error(Object cause);
}
