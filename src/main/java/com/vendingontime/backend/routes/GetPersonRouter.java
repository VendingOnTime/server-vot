package com.vendingontime.backend.routes;

import com.google.inject.Inject;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.GetPersonService;

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
public class GetPersonRouter extends AbstractGetRouter {

    public static final String V1_USERS_PROFILE = V1 + "/users/profile";

    @Inject
    public GetPersonRouter(ServiceResponse serviceResponse,
                           GetPersonService service, EndpointProtector protector) {
        super(serviceResponse, service, protector, V1_USERS_PROFILE);
    }
}
