package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.AssignMaintainerService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData.EMPTY_MACHINE_ID;
import static com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData.EMPTY_REQUESTER;
import static com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData.EMPTY_TECHNICIAN_ID;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
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

public class AssignMaintainerServiceTest {

    private static final String MACHINE_ID = "MACHINE_ID";
    private static final String TECHNICIAN_ID = "TECHNICIAN_ID";

    private MachineRepository machineRepository;
    private PersonRepository personRepository;
    private AuthProvider authProvider;

    private AssignMaintainerService assignMaintainerService;

    private Machine machine;
    private Person technician;
    private Person supervisor;

    private AssignMaintainerData assignMaintainerData;

    @Before
    public void setUp() throws Exception {
        machineRepository = mock(MachineRepository.class);
        personRepository = mock(PersonRepository.class);
        authProvider = mock(AuthProvider.class);

        assignMaintainerService = new AssignMaintainerService(personRepository, machineRepository, authProvider);

        machine = FixtureFactory.generateMachine().setId(MACHINE_ID);
        technician = FixtureFactory.generateTechnician().setId(TECHNICIAN_ID);
        supervisor = FixtureFactory.generateSupervisorWithCompany();
        supervisor.getCompany().addWorker(technician);

        assignMaintainerData = new AssignMaintainerData()
                .setId(machine.getId())
                .setTechnicianId(technician.getId())
                .setRequester(supervisor);

        when(machineRepository.findById(any())).thenReturn(Optional.empty());
        when(machineRepository.findById(machine.getId())).thenReturn(Optional.of(machine));

        when(personRepository.findById(any())).thenReturn(Optional.empty());
        when(personRepository.findById(technician.getId())).thenReturn(Optional.of(technician));
        when(personRepository.update(technician)).thenReturn(Optional.of(technician));

        when(authProvider.canModify(any(), any(Machine.class))).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        personRepository = null;
        machineRepository = null;
        authProvider = null;

        assignMaintainerService = null;

        machine = null;
        technician = null;
        supervisor = null;

        assignMaintainerData = null;
    }

    @Test
    public void assignMaintainer_withNoErrors_returnsModifiedMachine() throws Exception {
        Optional<Machine> machine = assignMaintainerService.assignMaintainer(assignMaintainerData);

        assertThat(machine.isPresent(), is(true));
        assertThat(machine.get().getMaintainer(), equalTo(technician));
    }

    @Test
    public void assignMaintainer_withInvalidData_throwsException() throws Exception {
        try {
            assignMaintainerService.assignMaintainer(new AssignMaintainerData());
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{EMPTY_MACHINE_ID, EMPTY_TECHNICIAN_ID, EMPTY_REQUESTER}));
        }
    }

    @Test
    public void assignMaintainer_withValidDataAndNotFoundMachine_returnsEmpty() throws Exception {
        assignMaintainerData.setId("ANOTHER_ID");
        Optional<Machine> machine = assignMaintainerService.assignMaintainer(assignMaintainerData);

        assertThat(machine.isPresent(), is(false));
    }

    @Test
    public void assignMaintainer_withValidDataAndUnauthorizedUser_throwsException() throws Exception {
        when(authProvider.canModify(supervisor, machine)).thenReturn(false);

        try {
            assignMaintainerService.assignMaintainer(assignMaintainerData);
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{INSUFFICIENT_PERMISSIONS}));
        }
    }

    @Test
    public void assignMaintainer_withValidDataAndNotFoundTechnician_returnsEmpty() throws Exception {
        assignMaintainerData.setTechnicianId("ANOTHER_ID");
        Optional<Machine> machine = assignMaintainerService.assignMaintainer(assignMaintainerData);

        assertThat(machine.isPresent(), is(false));
    }
}