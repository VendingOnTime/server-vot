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
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.GetMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;

import javax.inject.Inject;
import java.util.Optional;

public class GetMachineRouter extends AbstractGetRouter {
    public static final String V1_MACHINES = V1 + "/machines/";

    @Inject
    public GetMachineRouter(ServiceResponse serviceResponse,
                               GetMachineService service, EndpointProtector protector) {
        super(serviceResponse, service, protector, V1_MACHINES);
    }
}
