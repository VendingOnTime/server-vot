package integration.com.vendingontime.backend.routes;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.LogInRouter;
import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.JWTTokenGenerator;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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
public class LogInRouterTest extends IntegrationTest {

    @Inject private SignUpService signUpService;
    @Inject private LogInRouter logInRouter;
    @Inject private PersonRepository repository;

    private ObjectMapper mapper;
    private SignUpData signUpData;
    private LogInData logInData;
    private String stringifiedLogInData;

    @Before
    public void setUp() throws Exception {

        mapper = new ObjectMapper();

        signUpData = FixtureFactory.generateSignUpData();

        logInData = FixtureFactory.generateLogInDataFrom(signUpData);

        stringifiedLogInData = mapper.writeValueAsString(logInData);
    }

    @After
    public void tearDown() throws Exception {
        mapper = null;
        signUpData = null;
        logInData = null;
        stringifiedLogInData = null;
    }

    @Test
    public void logInUser() throws Exception {
        signUpService.createSupervisor(signUpData);

        String result = (String) logInRouter.logInUser(stringifiedLogInData)
                .handle(mock(Request.class), mock(Response.class));

        RESTResult restResult = mapper.readValue(result, RESTResult.class);

        assertTrue(restResult.getSuccess());
        String token = (String) restResult.getData();
        String tokenEmail = JWT.decode(token).getClaim(JWTTokenGenerator.EMAIL_CLAIM).asString();
        assertThat(tokenEmail, equalTo(logInData.getEmail()));

        Optional<Person> byEmail = repository.findByEmail(logInData.getEmail());
        repository.delete(byEmail.get().getId());
    }

}