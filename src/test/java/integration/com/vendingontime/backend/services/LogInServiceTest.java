package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.*;

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
public class LogInServiceTest extends IntegrationTest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;
    @Inject private PersonRepository repository;

    private SignUpData signUpData;
    private LogInData logInData;

    @Before
    public void setUp() throws Exception {
        signUpData = FixtureFactory.generateSignUpData();
        logInData = FixtureFactory.generateLogInDataFrom(signUpData);
    }

    @After
    public void tearDown() throws Exception {
        signUpData = null;
        logInData = null;
    }

    @Test
    public void authorizeUser() throws Exception {
        signUpService.createSupervisor(signUpData);

        String token = logInService.authorizeUser(logInData);
        assertNotNull(token);

        Optional<Person> byEmail = repository.findByEmail(logInData.getEmail());
        repository.delete(byEmail.get().getId());
    }

}