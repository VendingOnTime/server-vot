package com.vendingontime.backend.routes;

import com.google.inject.Inject;
import com.vendingontime.backend.middleware.EndpointProtector;
import com.vendingontime.backend.middleware.TokenEndpointProtector;
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.EditPersonService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;

import java.io.IOException;
import java.util.Optional;

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
public class EditPersonRouter extends AbstractSparkRouter {

    public static final String V1_EDIT_PROFILE = V1 + "/users/profile/";

    private final EditPersonService service;
    private final EndpointProtector protector;

    @Inject
    public EditPersonRouter(ServiceResponse serviceResponse, EditPersonService service, EndpointProtector protector) {
        super(serviceResponse);
        this.service = service;
        this.protector = protector;
    }

    @Override
    public void configure(Service http) {
        protector.protect(V1_EDIT_PROFILE + ID_PARAM);
        http.put(V1_EDIT_PROFILE + ID_PARAM, map((req, res) ->
                editProfile(req.params(ID_PARAM), req.body(), req.attribute(TokenEndpointProtector.LOGGED_IN_PERSON))));
    }

    public AppRoute editProfile(String id, String body, Person requester) {
        try {
            EditPersonData editPersonData = mapper.readValue(body, EditPersonData.class);
            editPersonData.setId(id);
            editPersonData.setRequester(requester);

            Optional<Person> possiblePerson = service.updatePerson(editPersonData);
            return possiblePerson.map(serviceResponse::ok).orElseGet(serviceResponse::notFound);
        } catch (BusinessLogicException e) {
            return serviceResponse.badRequest(e.getCauses());
        } catch (IOException e) {
            return serviceResponse.badRequest(MALFORMED_JSON);
        }
    }
}
