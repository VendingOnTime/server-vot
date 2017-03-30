package com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import javax.inject.Inject;
import java.io.IOException;

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
public class SignUpRouter implements SparkRouter {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    public static final String V1_SIGN_UP_SUPERVISOR = "/api-v1/signup/supervisor";

    private final SignUpService service;
    private final ServiceResponse serviceResponse;
    private final ObjectMapper mapper;

    @Inject
    public SignUpRouter(SignUpService service, ServiceResponse serviceResponse) {
        this.service = service;
        this.serviceResponse = serviceResponse;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void configure(Service http) {
        http.post(V1_SIGN_UP_SUPERVISOR, map((req, res) -> signUpSupervisor(req.body())));
    }

    public AppRoute signUpSupervisor(String body) {
        try {
            SignUpData supervisorCandidate = mapper.readValue(body, SignUpData.class);

            return serviceResponse.created(service.createSupervisor(supervisorCandidate));
        } catch (BusinessLogicException ex) {
            return serviceResponse.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return serviceResponse.badRequest(MALFORMED_JSON);
        }
    }

    private Route map(Converter c) {
        return (req, res) -> c.convert(req, res).handle(req,res);
    }

    private interface Converter {
        public AppRoute convert(Request req, Response res);
    }
}
