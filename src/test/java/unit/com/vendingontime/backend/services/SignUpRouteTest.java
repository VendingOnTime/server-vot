package unit.com.vendingontime.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonCollisionException;

import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.SignUpRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;

import org.junit.*;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
import static com.vendingontime.backend.models.person.PersonCollisionException.*;
import static com.vendingontime.backend.services.SignUpRoute.MALFORMED_JSON;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
public class SignUpRouteTest {
    private static final String DNI = "12345678B";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "username@test.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final PersonRole ROLE = PersonRole.SUPERVISOR;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PersonRepository repository;
    private ServiceResponse response;
    private SignUpRoute signUp;
    private SignUpData payload;

    private Person person;
    private String stringifiedPerson;

    @Before
    public void setUp() throws Exception {
        repository = mock(PersonRepository.class);
        response = mock(ServiceResponse.class);
        signUp = new SignUpRoute(repository, response);

        payload = new SignUpData();
        payload.setDni(DNI);
        payload.setUsername(USERNAME);
        payload.setEmail(EMAIL);
        payload.setName(NAME);
        payload.setSurnames(SURNAME);
        payload.setPassword(PASSWORD);
        payload.setRole(ROLE);

        person = new Person(payload);

        stringifiedPerson = objectMapper.writeValueAsString(payload);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        response = null;
        signUp = null;
        payload = null;
        stringifiedPerson = null;
    }

    @Test
    public void post() {
        signUp.post(stringifiedPerson);

        verify(response, times(1)).created(person);
        verify(repository, times(1)).create(person);
    }

    @Test
    public void post_withEmptyJSON() {
        stringifiedPerson = "";

        signUp.post(stringifiedPerson);

        verify(response, never()).created(person);
        verify(response, times(1)).badRequest(any());
        verify(repository, never()).create(person);
    }

    @Test
    public void post_withInvalidJSONField() {
        stringifiedPerson = "{\"id\":\"1234\"}";

        signUp.post(stringifiedPerson);

        verify(response, never()).created(person);
        verify(response, times(1)).badRequest(MALFORMED_JSON);
        verify(repository, never()).create(person);
    }

    //TODO incorrect data tests?

    @Test
    @Ignore
    public void post_withExistingUniqueData() {
        List<Cause> causes = new LinkedList<>();
        causes.add(Cause.DNI);
        causes.add(Cause.EMAIL);
        causes.add(Cause.USERNAME);

        String[] stringCauses = new String[] {DNI_EXISTS, EMAIL_EXISTS, USERNAME_EXISTS};

        signUp.post(stringifiedPerson);

        doThrow(new PersonCollisionException(causes.toArray(new Cause[causes.size()]))).when(repository).create(person);

        signUp.post(stringifiedPerson);

        verify(response, times(1)).badRequest(stringCauses);
        verify(repository, times(2)).create(person);
    }
}