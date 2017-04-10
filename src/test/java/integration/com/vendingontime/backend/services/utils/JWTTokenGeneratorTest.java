package integration.com.vendingontime.backend.services.utils;

import com.auth0.jwt.JWT;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.JWTTokenGenerator;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

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

public class JWTTokenGeneratorTest extends IntegrationTest {

    private static final String EMAIL = "supervisor@example.com";
    private static final String USERNAME = "supervisor1";
    private static final String PASSWORD = "password";

    @Inject
    private JWTTokenGenerator tokenGenerator;

    @Inject
    private SignUpService service;

    @Inject
    private PersonRepository repository;

    private SignUpData signUpData;
    private LogInData logInData;

    @Before
    public void setUp() throws Exception {
        signUpData = new SignUpData()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        logInData = new LogInData()
                .setEmail(EMAIL)
                .setPassword(PASSWORD);
    }

    @After
    public void tearDown() throws Exception {
        signUpData = null;
        logInData = null;
    }

    @Test
    public void generate() throws Exception {
        String token = tokenGenerator.generateFrom(logInData);
        String tokenEmail = JWT.decode(token).getClaim(JWTTokenGenerator.EMAIL_CLAIM).asString();
        assertThat(tokenEmail, equalTo(EMAIL));
    }

    @Test
    public void generate_twoDoNotMatch() throws Exception {
        String token1 = tokenGenerator.generateFrom(logInData);
        Thread.sleep(1000);
        String token2 = tokenGenerator.generateFrom(logInData);

        assertNotEquals(token1, token2);
    }

    @Test
    public void recoverFrom() throws Exception {
        String token = tokenGenerator.generateFrom(logInData);
        Person supervisor = service.createSupervisor(signUpData);

        Optional<Person> possiblePerson = tokenGenerator.recoverFrom(token);
        assertTrue(possiblePerson.isPresent());
        assertThat(possiblePerson.get(), equalTo(supervisor));

        repository.delete(supervisor.getId());
    }

}