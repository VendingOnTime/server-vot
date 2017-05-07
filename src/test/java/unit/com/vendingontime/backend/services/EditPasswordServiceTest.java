package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.EditPasswordData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.EditPasswordService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

public class EditPasswordServiceTest {
    private static final String SUPERVISOR_ID = "SUPERVISOR_ID";

    private AuthProvider authProvider;
    private PersonRepository repository;
    private EditPasswordService editPasswordService;

    private Person supervisor;
    private EditPasswordData editPasswordData;

    @Before
    public void setUp() throws Exception {
        authProvider = mock(AuthProvider.class);
        repository = mock(PersonRepository.class);
        editPasswordService = new EditPasswordService(authProvider, repository);

        supervisor = FixtureFactory.generateSupervisor().setId(SUPERVISOR_ID);

        editPasswordData = FixtureFactory.generateEditPasswordDataFrom(supervisor);
        editPasswordData.setNewPassword("NEW_PASSWORD");
        editPasswordData.setRequester(supervisor);

        when(authProvider.canModifyPassword(supervisor, supervisor)).thenReturn(true);

        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(repository.findById(SUPERVISOR_ID)).thenReturn(Optional.of(supervisor));

        when(repository.update(any())).thenReturn(Optional.of(supervisor));
    }

    @After
    public void tearDown() throws Exception {
        authProvider = null;
        repository = null;
        editPasswordService = null;
        supervisor = null;
        editPasswordData = null;
    }

    @Test
    public void editPerson_validDataAndPermissions_returnsFilledOptional() throws Exception {
        Optional<Person> possiblePerson = editPasswordService.updatePassword(editPasswordData);
        assertThat(possiblePerson.isPresent(), is(true));
    }

    @Test
    public void editPerson_invalidData_throwsException() throws Exception {
        editPasswordData.setNewPassword("");

        try {
            editPasswordService.updatePassword(editPasswordData);
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{EditPasswordData.EMPTY_NEW_PASSWORD}, e.getCauses());
        }
    }

    @Test
    public void editPerson_unknownPerson_returnsEmptyOptional() throws Exception {
        editPasswordData.setId("ANOTHER_PERSON");

        Optional<Person> possiblePerson = editPasswordService.updatePassword(editPasswordData);
        assertThat(possiblePerson.isPresent(), is(false));
    }

    @Test
    public void editPerson_insufficientPermissions_throwsException() throws Exception {
        when(authProvider.canModifyPassword(any(), any())).thenReturn(false);

        try {
            editPasswordService.updatePassword(editPasswordData);
        } catch (BusinessLogicException e) {
            assertArrayEquals(new String[]{EditPasswordService.INSUFFICIENT_PERMISSIONS}, e.getCauses());
        }
    }
}