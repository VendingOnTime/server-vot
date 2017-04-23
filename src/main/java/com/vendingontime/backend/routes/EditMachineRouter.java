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

import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.middleware.TokenEndpointProtector;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.EditMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

public class EditMachineRouter extends AbstractSparkRouter {

    // TODO: 23/4/17 Grab this from the parent class
    private static final String ID_PARAM = ":id";
    public static final String V1_EDIT_MACHINES = V1 + "/machines/";

    private final EditMachineService service;
    private final EndpointProtector protector;

    @Inject
    public EditMachineRouter(ServiceResponse serviceResponse,
                             EditMachineService service, EndpointProtector protector) {
        super(serviceResponse);
        this.service  = service;
        this.protector = protector;
    }

    @Override
    public void configure(Service http) {
        protector.protect(V1_EDIT_MACHINES + ID_PARAM);
        http.put(V1_EDIT_MACHINES + ID_PARAM,
                map((req, res) -> updateMachine(req.body(), req.attribute(TokenEndpointProtector.LOGGED_IN_PERSON))));
    }

    // FIXME: 24/4/17 Should the ID be passed as a parameter too?
    public AppRoute updateMachine(String body, Person requester) {
        try {
            EditMachineData editMachineData = mapper.readValue(body, EditMachineData.class);
            editMachineData.setRequester(requester);

            Optional<Machine> possibleMachine = service.updateMachine(editMachineData);
            return possibleMachine.map(serviceResponse::ok).orElseGet(serviceResponse::notFound);
        } catch (BusinessLogicException e) {
            return serviceResponse.badRequest(e.getCauses());
        } catch (IOException e) {
            return serviceResponse.badRequest(MALFORMED_JSON);
        }
    }
}
