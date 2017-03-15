package com.vendingontime.backend.routes.utils;

import spark.ResponseTransformer;

import static com.vendingontime.backend.routes.utils.ResponseWrapper.Type;

/**
 * Created by miguel on 13/3/17.
 */
public class ResponseGenerator {
    private static final String UNAUTHORIZED = "UNAUTHORIZED";
    private static final String NOT_FOUND = "NOT_FOUND";

    private final String contentType;
    private final ResponseTransformer transformer;

    public ResponseGenerator(String contentType, ResponseTransformer transformer) {
        this.contentType = contentType;
        this.transformer = transformer;
    }

    public AppRoute ok(Object body) {
        return response(new ResponseWrapper(Type.OK, body), StatusCode.OK);
    }

    public AppRoute created(Object body) {
        return response(new ResponseWrapper(Type.OK, body), StatusCode.CREATED);
    }

    public AppRoute badRequest(Object cause) {
        return response(new ResponseWrapper(Type.ERROR, cause), StatusCode.BAD_REQUEST);
    }

    public AppRoute unauthorized() {
        return response(new ResponseWrapper(Type.ERROR, UNAUTHORIZED), StatusCode.UNAUTHORIZED);
    }

    public AppRoute notFound() {
        return response(new ResponseWrapper(Type.ERROR, NOT_FOUND), StatusCode.NOT_FOUND);
    }

    private AppRoute response(Object body, int status) {
        return (req, res) -> {
            res.status(status);
            res.type(contentType);
            return transformer.render(body);
        };
    }

    private static class StatusCode {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
    }
}
