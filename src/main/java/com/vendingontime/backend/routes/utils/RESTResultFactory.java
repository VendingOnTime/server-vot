package com.vendingontime.backend.routes.utils;

/**
 * Created by alberto on 15/3/17.
 */
public class RESTResultFactory implements ResultFactory {


    @Override
    public Object ok(Object data) {
        return new RESTResult(true, data);
    }

    @Override
    public Object error(Object cause) {
        return new RESTResult(false, cause);
    }
}
