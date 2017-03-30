package com.vendingontime.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.utils.AppRoute;

import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.utils.BusinessLogicException;

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
public class SignUpRoute {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    private final ObjectMapper mapper = new ObjectMapper();
    private PersonRepository repository;
    private ServiceResponse response;

    public SignUpRoute(PersonRepository repository, ServiceResponse response) {
        this.repository = repository;
        this.response = response;
    }

    public AppRoute post(String requestBody) {
        try {
            SignUpData personCandidate = mapper.readValue(requestBody, SignUpData.class);

            return createUser(personCandidate);
        } catch (BusinessLogicException ex) {
            return response.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return response.badRequest(MALFORMED_JSON);
        }
    }

    private AppRoute createUser(SignUpData personCandidate) throws BusinessLogicException {
        String[] signUpErrors = personCandidate.validate();
        if(signUpErrors.length != 0) {
            throw new BusinessLogicException(signUpErrors);
        }

        Person person = new Person(personCandidate);

        try {
            repository.create(person);
        } catch (PersonCollisionException ex) {
            throw new BusinessLogicException(ex.getCauses());
        }

        return response.created(person);
    }
}
