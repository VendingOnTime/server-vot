package com.vendingontime.backend.routes;

import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;

import javax.inject.Inject;
import java.io.IOException;

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
public class SignUpRouter extends AbstractSparkRouter {

    public static final String V1_SIGN_UP_SUPERVISOR = V1 + "/signup/supervisor";

    private final SignUpService service;

    @Inject
    public SignUpRouter(SignUpService service, ServiceResponse serviceResponse) {
        super(serviceResponse);
        this.service = service;
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
}
