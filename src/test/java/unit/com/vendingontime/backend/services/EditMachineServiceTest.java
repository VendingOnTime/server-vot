package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.EditMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

public class EditMachineServiceTest {
    private MachineRepository repository;

    private EditMachineService editMachineService;
    private EditMachineData editMachineData;
    private Machine machine;

    @Before
    public void setUp() throws Exception {
        repository = mock(MachineRepository.class);

        editMachineService = new EditMachineService(repository);

        editMachineData = FixtureFactory.generateEditMachineData();

        editMachineData.setRequester(FixtureFactory.generateSupervisorWithCompany());
        editMachineData.getRequester().getCompany().setId("COMPANY_ID");
        editMachineData.getRequester().setId("PERSON_ID");

        machine = new Machine(editMachineData.setId("MACHINE_ID"))
                .setCompany(editMachineData.getRequester().getCompany());

        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(repository.findById(machine.getId())).thenReturn(Optional.of(machine));
        when(repository.update(any())).thenReturn(Optional.of(machine));
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        editMachineService = null;
        editMachineData = null;
    }

    @Test
    public void editMachine() {
        Optional<Machine> possibleMachine = editMachineService.updateMachine(editMachineData);

        assertThat(possibleMachine.isPresent(), is(true));

        verify(repository, times(1)).findById(editMachineData.getId());
        verify(repository, times(1)).update(any());
    }

    @Test
    public void editMachine_withInvalidPayload_throwsException() {
        try {
            editMachineData.setId(" ");
            editMachineService.updateMachine(editMachineData);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EditMachineData.EMPTY_MACHINE_ID}, ex.getCauses());

            verify(repository, never()).findById(any());
            verify(repository, never()).update(any());
        }
    }

    @Test
    public void editMachine_withInvalidRequester_throwsException() {
        try {
            editMachineData.setRequester(null);
            editMachineService.updateMachine(editMachineData);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EditMachineService.INSUFFICIENT_PERMISSIONS}, ex.getCauses());

            verify(repository, never()).findById(any());
            verify(repository, never()).update(any());
        }
    }

    @Test
    public void editMachine_withUnknownId_returnsEmpty() throws Exception {
            editMachineData.setId("UNKNOWN_ID");
        Optional<Machine> possibleUpdate = editMachineService.updateMachine(editMachineData);

        assertThat(possibleUpdate.isPresent(), is(false));

        verify(repository, times(1)).findById(editMachineData.getId());
        verify(repository, never()).update(any());
    }

    @Test
    public void editMachine_withDifferentCompany_throwsException() {
        try {
            editMachineData.getRequester().setCompany(FixtureFactory.generateCompany().setId("ANOTHER_COMPANY_ID"));
            editMachineService.updateMachine(editMachineData);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EditMachineService.INSUFFICIENT_PERMISSIONS}, ex.getCauses());

            verify(repository, times(1)).findById(editMachineData.getId());
            verify(repository, never()).update(any());
        }
    }
}