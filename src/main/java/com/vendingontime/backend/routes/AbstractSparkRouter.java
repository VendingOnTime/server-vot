package com.vendingontime.backend.routes;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractSparkRouter implements SparkRouter {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    protected final static String V1 = "/api-v1";

    protected static final String ID_PARAM = ":id";

    protected final ServiceResponse serviceResponse;
    protected final ObjectMapper mapper;


    protected AbstractSparkRouter(ServiceResponse serviceResponse) {
        this.serviceResponse = serviceResponse;
        this.mapper = new ObjectMapper();
    }

    protected Route map(Converter c) {
        return (req, res) -> c.convert(req, res).handle(req,res);
    }

    protected interface Converter {
        AppRoute convert(Request req, Response res);
    }
}
