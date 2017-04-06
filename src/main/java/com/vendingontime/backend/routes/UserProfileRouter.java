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
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import spark.Service;

import javax.inject.Inject;

public class UserProfileRouter extends AbstractSparkRouter {

    public static final String V1_PROFILE = V1 + "/users/profile";

    private final EndpointProtector endpointProtector;

    @Inject
    public UserProfileRouter(ServiceResponse serviceResponse, EndpointProtector endpointProtector) {
        super(serviceResponse);
        this.endpointProtector = endpointProtector;
    }

    @Override
    public void configure(Service http) {
        endpointProtector.protect(V1_PROFILE);
        http.get(V1_PROFILE, map((req, res) -> retrieveUserProfile(req.attribute(TokenEndpointProtector.LOGGED_IN_PERSON))));
    }

    public AppRoute retrieveUserProfile(Person person) {
        return serviceResponse.ok(person);
    }
}
