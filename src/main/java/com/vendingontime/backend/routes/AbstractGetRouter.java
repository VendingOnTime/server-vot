package com.vendingontime.backend.routes;

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.middleware.TokenEndpointProtector;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.AbstractGetService;
import com.vendingontime.backend.services.GetMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;

import java.util.Optional;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class AbstractGetRouter extends AbstractSparkRouter {
    private final AbstractGetService service;
    private final EndpointProtector protector;
    private final String endpointPath;

    public AbstractGetRouter(ServiceResponse serviceResponse,
                            GetMachineService service, EndpointProtector protector, String endpointPath) {
        super(serviceResponse);
        this.service = service;
        this.protector = protector;
        this.endpointPath = endpointPath;
    }

    @Override
    public void configure(Service http) {
        protector.protect(endpointPath + ID_PARAM);
        http.get(endpointPath + ID_PARAM, map((req, res) ->
                getWith(req.params(ID_PARAM), req.attribute(TokenEndpointProtector.LOGGED_IN_PERSON))));
    }

    public AppRoute getWith(String id, Person requester) {
        try {
            PersonRequest personRequest = new PersonRequest().setId(id).setRequester(requester);
            Optional<?> machineCandidate = service.getBy(personRequest);
            return machineCandidate.map(serviceResponse::ok).orElseGet(serviceResponse::notFound);
        } catch (BusinessLogicException ex) {
            return serviceResponse.badRequest(ex.getCauses());
        }
    }
}
