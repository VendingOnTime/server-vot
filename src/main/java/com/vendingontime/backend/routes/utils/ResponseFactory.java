package com.vendingontime.backend.routes.utils;

/**
 * Created by Alberto on 13/03/2017.
 */
public class ResponseFactory {
    private static final String APPLICATION_JSON = "application/json";

    public static HttpResponse json() {
        return new HttpResponse(APPLICATION_JSON, new JSONTransformer());
    }
}
