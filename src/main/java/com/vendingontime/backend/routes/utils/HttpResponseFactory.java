package com.vendingontime.backend.routes.utils;

/**
 * Created by Alberto on 13/03/2017.
 */
public class HttpResponseFactory {
    private volatile static Response response;
    private static final String APPLICATION_JSON = "application/json";

    public static Response json() {
        if( response == null) {
            synchronized (HttpResponseFactory.class) {
                if (response == null) {
                    response = new HttpResponse(APPLICATION_JSON, new JSONTransformer(), new RESTResultFactory());
                }
            }
        }

        return response;
    }
}
