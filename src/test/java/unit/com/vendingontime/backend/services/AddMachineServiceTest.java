package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.*;
import com.vendingontime.backend.services.AbstractService;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.hamcrest.core.IsNull.notNullValue;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
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

public class AddMachineServiceTest {
    private MachineRepository repository;
    private CompanyRepository companyRepository;

    private AddMachineService addMachineService;
    private AddMachineData addMachineData;
    private Machine machine;

    @Before
    public void setUp() throws Exception {
        repository = mock(MachineRepository.class);
        companyRepository = mock(CompanyRepository.class);

        addMachineService = new AddMachineService(repository, companyRepository);

        addMachineData = FixtureFactory.generateAddMachineData()
                .setRequester(FixtureFactory.generateSupervisorWithCompany());
        addMachineData.getRequester().getOwnedCompany().setId("COMPANY_ID");
        addMachineData.getRequester().setId("PERSON_ID");

        machine = new Machine(addMachineData);

        when(repository.create(any())).thenReturn(machine.setId("MACHINE_ID"));
        when(repository.findById(machine.getId())).thenReturn(Optional.of(machine));
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        companyRepository = null;
        addMachineService = null;
        addMachineData = null;
    }

    @Test
    public void createMachine() {
        Machine machine = addMachineService.createMachine(addMachineData);

        assertThat(machine.getId(), notNullValue());

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(1)).update(addMachineData.getRequester().getOwnedCompany());
    }

    @Test
    public void createMachine_withInvalidPayload_throwsException() {
        try {
            addMachineData.getLocation().setName("");
            addMachineService.createMachine(addMachineData);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{AddMachineData.SHORT_LOCATION_NAME}, ex.getCauses());

            verify(repository, never()).create(any());
            verify(companyRepository, never()).update(any());
        }
    }

    @Test
    public void createMachine_withInvalidRequester_throwsException() {
        try {
            addMachineService.createMachine(addMachineData.setRequester(null));
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{AbstractService.INSUFFICIENT_PERMISSIONS}, ex.getCauses());

            verify(repository, never()).create(machine);
            verify(companyRepository, never()).update(any());
        }
    }
}