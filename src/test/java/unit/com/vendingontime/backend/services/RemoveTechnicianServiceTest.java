package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.RemoveMachineService;
import com.vendingontime.backend.services.RemoveTechnicianService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.PersonRequest.EMPTY_REQUESTER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
public class RemoveTechnicianServiceTest {
    private PersonRepository repository;

    private RemoveTechnicianService removeTechnicianService;
    private AuthProvider authProvider;

    private Person technician;
    private Person requester;
    private PersonRequest personRequest;

    @Before
    public void setUp() throws Exception {
        repository = mock(PersonRepository.class);
        authProvider = mock(AuthProvider.class);

        removeTechnicianService = new RemoveTechnicianService(repository, authProvider);

        Company company = FixtureFactory.generateCompanyWithOwner();
        requester = company.getOwner();
        technician = FixtureFactory.generateTechnician().setId("TECHNICIAN_ID");
        company.addWorker(technician);

        personRequest = FixtureFactory.generatePersonRequestFrom(technician, requester);

        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(repository.findById(technician.getId())).thenReturn(Optional.of(technician));
        when(repository.update(technician)).thenReturn(Optional.of(technician));

        when(authProvider.canModify(any(), any(AbstractEntity.class))).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        authProvider = null;

        removeTechnicianService = null;

        technician = null;
        requester = null;
        personRequest = null;
    }

    @Test
    public void removeTechnician() {
        Optional<Person> possibleRemoved = removeTechnicianService.removeWith(personRequest);

        assertThat(possibleRemoved.isPresent(), is(true));

        verify(repository, times(1)).findById(technician.getId());
        verify(repository, times(1)).update(any());

        Person savedPerson = repository.findById(technician.getId()).get();
        assertThat(savedPerson.isDisabled(), is(true));
    }

    @Test
    public void removeTechnician_withInvalidRequester_throwsException() {
        try {
            personRequest.setRequester(null);
            removeTechnicianService.removeWith(personRequest);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EMPTY_REQUESTER}, ex.getCauses());

            verify(repository, never()).findById(any());
            verify(repository, never()).update(any());
        }
    }

    @Test
    public void removeTechnician_withUnknownId_returnsEmpty() throws Exception {
        String unknownId = "UNKNOWN_ID";
        personRequest.setId(unknownId);
        Optional<Person> possibleRemoved = removeTechnicianService.removeWith(personRequest);

        assertThat(possibleRemoved.isPresent(), is(false));

        verify(repository, times(1)).findById(unknownId);
        verify(repository, never()).update(any());
    }

    @Test
    public void removeTechnician_requesterNotAuthorized_throwsException() {
        when(authProvider.canModify(requester, (AbstractEntity) technician)).thenReturn(false);

        try {
            requester.setOwnedCompany(FixtureFactory.generateCompany().setId("ANOTHER_COMPANY_ID"));
            removeTechnicianService.removeWith(personRequest);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{RemoveMachineService.INSUFFICIENT_PERMISSIONS}, ex.getCauses());

            verify(repository, times(1)).findById(technician.getId());
            verify(repository, never()).update(any());
        }
    }
}