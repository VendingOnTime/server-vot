package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;

import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.services.SignUpService;

import com.vendingontime.backend.services.utils.BusinessLogicException;

import static com.vendingontime.backend.models.person.PersonCollisionException.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

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
    private static final String DNI = "12345678B";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "username@test.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final PersonRole ROLE = PersonRole.SUPERVISOR;

    private JPAPersonRepository repository;
    private CompanyRepository companyRepository;
    private SignUpService signUp;
    private SignUpData payload;

    private Person person;
    private Company company;

    @Before
    public void setUp() throws Exception {
        final String PERSON_ID = "PERSON_ID";
        final String COMPANY_ID = "COMPANY_ID";

        repository = mock(JPAPersonRepository.class);
        companyRepository = mock(CompanyRepository.class);

        signUp = new SignUpService(repository, companyRepository);

        payload = new SignUpData()
                .setDni(DNI)
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setName(NAME)
                .setSurnames(SURNAME)
                .setPassword(PASSWORD)
                .setRole(ROLE);

        person = new Person(payload);
        company = new Company();

        when(repository.create(any())).thenReturn(person.setId(PERSON_ID));
        when(repository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(companyRepository.create(any())).thenReturn(company.setId(COMPANY_ID));
        when(companyRepository.update(any())).thenReturn(Optional.of(company.setOwner(person)));
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        companyRepository = null;
        signUp = null;
        payload = null;
        person = null;
        company = null;
    }

    @Test
    public void createSupervisor() {
        signUp.createSupervisor(payload);

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(1)).create(any());
        verify(companyRepository, times(1)).update(any());

        assertNotNull(person.getCompany().getId());
        assertNotNull(companyRepository.findById(person.getCompany().getId()));
    }

    @Test
    public void createSupervisor_withNoRole_returnsSupervisor() throws Exception {
        payload.setRole(null);
        Person supervisor = signUp.createSupervisor(payload);

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(1)).create(any());
        verify(companyRepository, times(1)).update(any());

        assertEquals(PersonRole.SUPERVISOR, supervisor.getRole());
        assertNotNull(supervisor.getCompany().getId());
        assertNotNull(companyRepository.findById(supervisor.getCompany().getId()));
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