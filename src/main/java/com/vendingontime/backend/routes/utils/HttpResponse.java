package com.vendingontime.backend.routes.utils;

import com.vendingontime.backend.config.inject.ConfigModule;
import spark.ResponseTransformer;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class HttpResponse implements ServiceResponse {
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String NOT_FOUND = "NOT_FOUND";

    private final String contentType;
    private final ResponseTransformer transformer;
    private final ResultFactory resultFactory;

    @Inject
    public HttpResponse(@Named(ConfigModule.RESPONSE_CONTENT_TYPE) String contentType,
                        ResponseTransformer transformer, ResultFactory resultFactory) {
        this.contentType = contentType;
        this.transformer = transformer;
        this.resultFactory = resultFactory;
    }

    public AppRoute ok(Object body) {
        return response(resultFactory.ok(body), StatusCode.OK);
    }

    public AppRoute created(Object body) {
        return response(resultFactory.ok(body), StatusCode.CREATED);
    }

    public AppRoute badRequest(Object cause) {
        return response(resultFactory.error(cause), StatusCode.BAD_REQUEST);
    }

    public AppRoute unauthorized() {
        return response(resultFactory.error(UNAUTHORIZED), StatusCode.UNAUTHORIZED);
    }

    public AppRoute notFound() {
        return response(resultFactory.error(NOT_FOUND), StatusCode.NOT_FOUND);
    }

    private AppRoute response(Object body, int status) {
        return (req, res) -> {
            res.status(status);
            res.type(contentType);
            return transformer.render(body);
        };
    }

    public static class StatusCode {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
    }
}
