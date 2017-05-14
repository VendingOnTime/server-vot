package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.bodymodels.RemovalRequest;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.RemoveMachineService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.RemovalRequest.EMPTY_REQUESTER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

public class RemoveMachineServiceTest {
    private MachineRepository repository;

    private RemoveMachineService removeMachineService;
    private AuthProvider authProvider;

    private Machine machine;
    private Person requester;
    private RemovalRequest removalRequest;

    @Before
    public void setUp() throws Exception {
        repository = mock(MachineRepository.class);
        authProvider = mock(AuthProvider.class);

        removeMachineService = new RemoveMachineService(repository, authProvider);

        Company company = FixtureFactory.generateCompanyWithOwner();
        requester = company.getOwner();
        machine = FixtureFactory.generateMachine().setId("MACHINE_ID");
        company.addMachine(machine);

        removalRequest = FixtureFactory.generateRemovalRequestFrom(machine, requester);

        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(repository.findById(machine.getId())).thenReturn(Optional.of(machine));
        when(repository.update(machine)).thenReturn(Optional.of(machine));

        when(authProvider.canModify(any(), any(AbstractEntity.class))).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        authProvider = null;

        removeMachineService = null;

        machine = null;
        requester = null;
        removalRequest = null;
    }

    @Test
    public void removeMachine() {
        Optional<Machine> possibleRemoved = removeMachineService.remove(removalRequest);

        assertThat(possibleRemoved.isPresent(), is(true));

        verify(repository, times(1)).findById(machine.getId());
        verify(repository, times(1)).update(any());

        Machine savedMachine = repository.findById(machine.getId()).get();
        assertThat(savedMachine.isDisabled(), is(true));
    }

    @Test
    public void removeMachine_withInvalidRequester_throwsException() {
        try {
            removalRequest.setRequester(null);
            removeMachineService.remove(removalRequest);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EMPTY_REQUESTER}, ex.getCauses());

            verify(repository, never()).findById(any());
            verify(repository, never()).update(any());
        }
    }

    @Test
    public void removeMachine_withUnknownId_returnsEmpty() throws Exception {
        String unknownId = "UNKNOWN_ID";
        removalRequest.setId(unknownId);
        Optional<Machine> possibleRemoved = removeMachineService.remove(removalRequest);

        assertThat(possibleRemoved.isPresent(), is(false));

        verify(repository, times(1)).findById(unknownId);
        verify(repository, never()).update(any());
    }

    @Test
    public void removeMachine_requesterNotAuthorized_throwsException() {
        when(authProvider.canModify(requester, (AbstractEntity) machine)).thenReturn(false);

        try {
            requester.setOwnedCompany(FixtureFactory.generateCompany().setId("ANOTHER_COMPANY_ID"));
            removeMachineService.remove(removalRequest);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{RemoveMachineService.INSUFFICIENT_PERMISSIONS}, ex.getCauses());

            verify(repository, times(1)).findById(machine.getId());
            verify(repository, never()).update(any());
        }
    }
}