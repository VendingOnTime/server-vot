package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.person.Person;

import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.services.SignUpService;

import com.vendingontime.backend.services.utils.BusinessLogicException;

import static com.vendingontime.backend.models.person.PersonCollisionException.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

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
public class SignUpServiceTest {
    private JPAPersonRepository repository;
    private SignUpService signUp;
    private SignUpData payload;

    private Person person;

    @Before
    public void setUp() throws Exception {
        repository = mock(JPAPersonRepository.class);
        signUp = new SignUpService(repository);

        payload = FixtureFactory.generateSignUpData().setRole(PersonRole.SUPERVISOR);
        person = new Person(payload);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        signUp = null;
        payload = null;
        person = null;
    }

    @Test
    public void createSupervisor() {
        signUp.createSupervisor(payload);

        verify(repository, times(1)).create(person);
    }

    @Test
    public void createSupervisor_withNoRole_returnsSupervisor() throws Exception {
        payload.setRole(null);
        Person supervisor = signUp.createSupervisor(payload);

        verify(repository, times(1)).create(person);
        assertEquals(PersonRole.SUPERVISOR, supervisor.getRole());
    }

    @Test
    public void createSupervisor_withInvalidPayload_throwsException() throws Exception {
        try {
            payload.setEmail("");
            signUp.createSupervisor(payload);
            fail();
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{SignUpData.EMPTY_EMAIL}, e.getCauses());
        }
    }

    @Test
    public void createSupervisor_withCollision_throwsException() throws Exception {
        try {
            doThrow(new PersonCollisionException(new Cause[]{Cause.EMAIL}))
                    .when(repository).create(any());
            signUp.createSupervisor(payload);
            fail();
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{EMAIL_EXISTS}, e.getCauses());
        }
    }
}