package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.repositories.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Optional;

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
public class SignUpServiceTest extends IntegrationTest {

    @Inject
    SignUpService service;

    @Inject
    PersonRepository repository;

    private SignUpData supervisor;

    @Before
    public void setUp() throws Exception {
        supervisor = new SignUpData()
                .setRole(PersonRole.SUPERVISOR)
                .setEmail("user@example.com")
                .setUsername("user")
                .setPassword("12345")
                .setName("name")
                .setSurnames("surnames");

    }

    @After
    public void tearDown() throws Exception {
        supervisor = null;
    }

    @Test
    public void createUser() throws Exception {
        Person user = service.createSupervisor(supervisor);

        assertNotNull(user);

        Optional<Person> byEmail = repository.findByEmail(supervisor.getEmail());
        assertTrue(byEmail.isPresent());

        repository.delete(user.getId());
    }

}