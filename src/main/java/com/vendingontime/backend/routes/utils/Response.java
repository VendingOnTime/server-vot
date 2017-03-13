package com.vendingontime.backend.routes.utils;

/**
 * Created by miguel on 13/3/17.
 */
public class Response {
    private static final String CONTENT_TYPE = "application/json";
    private static class StatusCode {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
    }

    public static ResponseCreator ok(String body) {
        return response(body, StatusCode.OK);
    }

    public static ResponseCreator created(String body) {
        return response(body, StatusCode.CREATED);
    }

    public static ResponseCreator badRequest(String body) {
        return response(body, StatusCode.BAD_REQUEST);
    }

    public static ResponseCreator unauthorized(String body) {
        return response(body, StatusCode.UNAUTHORIZED);
    }

    public static ResponseCreator notFound(String body) {
        return response(body, StatusCode.NOT_FOUND);
    }

    private static ResponseCreator response(String body, int status) {
        return (req, res) -> {
            res.status(status);
            res.type(CONTENT_TYPE);

            return body;
        };
    }
}
