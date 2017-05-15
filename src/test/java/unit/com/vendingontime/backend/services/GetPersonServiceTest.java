package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.GetPersonService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.PersonRequest.EMPTY_REQUESTER;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class GetPersonServiceTest {
    private final String PERSON_ID = "PERSON_ID";

    private PersonRepository repository;
    private AuthProvider authProvider;
    private GetPersonService service;

    private Person requester;
    private Person person;
    private PersonRequest personRequest;

    @Before
    public void setUp() throws Exception {
        final String COMPANY_ID = "COMPANY_ID";
        final String OWNER_ID = "OWNER_ID";

        repository = mock(PersonRepository.class);
        authProvider = mock(AuthProvider.class);
        service = new GetPersonService(repository, authProvider);

        Company company = FixtureFactory.generateCompanyWithOwner().setId(COMPANY_ID);
        requester = company.getOwner().setId(OWNER_ID);
        person = FixtureFactory.generateTechnician().setId(PERSON_ID);
        company.addWorker(person);
        personRequest = FixtureFactory.generatePersonRequestFrom(person, requester);

        when(repository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(authProvider.canAccess(requester, person)).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        authProvider = null;
        service = null;

        requester = null;
        person = null;
        personRequest = null;
    }

    @Test
    public void getPersonData_forValidPersonId_andAuthorizedUser() {
        Person foundPerson = service.getBy(personRequest).get();

        verify(repository, times(1)).findById(PERSON_ID);
        assertThat(foundPerson, equalTo(person));
    }

    @Test
    public void getPersonData_forInvalidPersonId() {
        personRequest.setId("INVALID_ID");
        Optional<Person> foundPerson = service.getBy(personRequest);

        verify(repository, times(1)).findById("INVALID_ID");
        assertThat(foundPerson.isPresent(), is(false));
    }

    @Test
    public void getPersonData_forValidPersonId_andNotAuthorizedUser_throwException() {
        Person randomUser = FixtureFactory.generateCustomer().setId("RANDOM_USER");

        when(authProvider.canAccess(randomUser, person)).thenReturn(false);
        personRequest.setRequester(randomUser);
        try {
            service.getBy(personRequest);
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{INSUFFICIENT_PERMISSIONS}, ex.getCauses());
            verify(repository, times(1)).findById(PERSON_ID);
        }
    }

    @Test
    public void getPersonData_forNullPerson_throwException() {
        personRequest.setRequester(null);
        try {
            service.getBy(personRequest);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EMPTY_REQUESTER}, ex.getCauses());
            verify(repository, never()).findById(PERSON_ID);
        }
    }
}