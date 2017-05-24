package com.vendingontime.backend.routes;

import com.google.inject.Inject;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.middleware.TokenEndpointProtector;
import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.AssignMaintainerService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;


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
public class AssignMaintainerRouter extends AbstractSparkRouter {

    public static final String V1_MACHINES = V1 + "/machines/";
    public static final String MAINTAINER = "/maintainer";

    private final EndpointProtector protector;
    private final AssignMaintainerService assignMaintainerService;

    @Inject
    public AssignMaintainerRouter(ServiceResponse serviceResponse, EndpointProtector protector, AssignMaintainerService assignMaintainerService) {
        super(serviceResponse);
        this.protector = protector;
        this.assignMaintainerService = assignMaintainerService;
    }

    @Override
    public void configure(Service http) {
        protector.protect(V1_MACHINES + ID_PARAM + MAINTAINER);
        http.put(V1_MACHINES + ID_PARAM + MAINTAINER, map((req, res) ->
                assignMachineMaintainer(req.params(ID_PARAM), req.body(),
                        req.attribute(TokenEndpointProtector.LOGGED_IN_PERSON))));
    }

    public AppRoute assignMachineMaintainer(String id, String body, Person requester) {
        try {
            AssignMaintainerData assignMaintainerData = mapper.readValue(body, AssignMaintainerData.class);
            assignMaintainerData.setId(id).setRequester(requester);
            return assignMaintainerService.assignMaintainer(assignMaintainerData)
                    .map(serviceResponse::ok).orElseGet(serviceResponse::notFound);
        } catch (BusinessLogicException e) {
            return serviceResponse.badRequest(e.getCauses());
        } catch (IOException e) {
            return serviceResponse.badRequest(MALFORMED_JSON);
        }
    }
}
