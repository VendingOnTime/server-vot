package com.vendingontime.backend.initializers.sparkplugins;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

/*
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
public class CORSPlugin implements SparkPlugin {

    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    // FIXME: 30/3/17 Make this more restrictive
    private static final String ORIGIN = "*";
    private static final String METHODS = "GET, POST, PUT, DELETE";
    private static final String HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization";

    @Override
    public void enable(Service http) {
        http.options("/*", this::options);
        http.before(this::before);
    }

    public String options(Request request, Response response) {
        String accessControlRequestHeaders = request.headers(ACCESS_CONTROL_REQUEST_HEADERS);
        if (accessControlRequestHeaders != null) {
            response.header(ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
        }

        String accessControlRequestMethod = request.headers(ACCESS_CONTROL_REQUEST_METHOD);
        if (accessControlRequestMethod != null) {
            response.header(ACCESS_CONTROL_ALLOW_METHODS, accessControlRequestMethod);
        }

        return "OK";
    }

    public void before(Request request, Response response) {
        response.header(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN);
        response.header(ACCESS_CONTROL_REQUEST_METHOD, METHODS);
        response.header(ACCESS_CONTROL_ALLOW_HEADERS, HEADERS);
    }
}
