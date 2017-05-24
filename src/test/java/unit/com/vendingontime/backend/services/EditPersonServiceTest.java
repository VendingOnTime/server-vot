package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.EditPersonService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
public class EditPersonServiceTest {

    private static final String SUPERVISOR_ID = "SUPERVISOR_ID";

    private AuthProvider authProvider;
    private PersonRepository repository;
    private EditPersonService editPersonService;

    private Person supervisor;
    private EditPersonData editPersonData;

    @Before
    public void setUp() throws Exception {
        authProvider = mock(AuthProvider.class);
        repository = mock(PersonRepository.class);
        editPersonService = new EditPersonService(authProvider, repository);

        supervisor = FixtureFactory.generateSupervisor().setId(SUPERVISOR_ID);

        editPersonData = FixtureFactory.generateEditPersonDataFrom(supervisor);
        editPersonData.setRequester(supervisor);

        when(authProvider.canModify(supervisor, supervisor)).thenReturn(true);

        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(repository.findById(SUPERVISOR_ID)).thenReturn(Optional.of(supervisor));

        when(repository.update(any())).thenReturn(Optional.of(supervisor));
    }

    @After
    public void tearDown() throws Exception {
        authProvider = null;
        repository = null;
        editPersonService = null;
        supervisor = null;
        editPersonData = null;
    }

    @Test
    public void editPerson_validDataAndPermissions_returnsFilledOptional() throws Exception {
        Optional<Person> possiblePerson = editPersonService.updatePerson(editPersonData);
        assertThat(possiblePerson.isPresent(), is(true));
    }

    @Test
    public void editPerson_invalidData_throwsException() throws Exception {
        editPersonData.setId(null);

        try {
            editPersonService.updatePerson(editPersonData);
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{EditPersonData.EMPTY_PERSON_ID}, e.getCauses());
        }
    }

    @Test
    public void editPerson_unknownPerson_returnsEmptyOptional() throws Exception {
        editPersonData.setId("ANOTHER_PERSON");

        Optional<Person> possiblePerson = editPersonService.updatePerson(editPersonData);
        assertThat(possiblePerson.isPresent(), is(false));
    }

    @Test
    public void editPerson_insufficientPermissions_throwsException() throws Exception {
        when(authProvider.canModify(any(Person.class), any(Person.class))).thenReturn(false);

        try {
            editPersonService.updatePerson(editPersonData);
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{EditPersonService.INSUFFICIENT_PERMISSIONS}, e.getCauses());
        }
    }

    @Test
    public void editPerson_password_shouldNotChange() throws Exception {
        editPersonData.setPassword("ANOTHER_PASSWORD");

        editPersonService.updatePerson(editPersonData);
        assertThat(editPersonData.getPassword(), equalTo(supervisor.getPassword()));
    }

    @Test
    public void editPerson_role_shouldNotChange() throws Exception {
        editPersonData.setRole(PersonRole.CUSTOMER);

        editPersonService.updatePerson(editPersonData);
        assertThat(editPersonData.getRole(), equalTo(supervisor.getRole()));
    }
}