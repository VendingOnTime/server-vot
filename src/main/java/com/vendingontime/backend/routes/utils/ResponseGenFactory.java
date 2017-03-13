package com.vendingontime.backend.routes.utils;

/**
 * Created by Alberto on 13/03/2017.
 */
public class ResponseGenFactory {
    private static final String APPLICATION_JSON = "application/json";

    public static ResponseGenerator json() {
        return new ResponseGenerator(APPLICATION_JSON, new JSONTransformer());
    }
}
