package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;
import com.vendingontime.backend.repositories.JPAMachineRepository;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String LOCATION_NAME = "LOCATION_NAME";
    private static final MachineLocation MACHINE_LOCATION = new MachineLocation().setName(LOCATION_NAME);

    private JPAMachineRepository repository;
    private AddMachineService addMachineService;
    private AddMachineData addMachineData;
    private Machine machine;

    @Before
    public void setUp() throws Exception {
        repository = mock(JPAMachineRepository.class);

        addMachineService = new AddMachineService(repository);

        addMachineData = new AddMachineData();
        addMachineData.setDescription(DESCRIPTION);
        addMachineData.setMachineLocation(MACHINE_LOCATION);
        addMachineData.setMachineType(MachineType.COFFEE);
        addMachineData.setMachineState(MachineState.OPERATIVE);

        machine = new Machine(addMachineData);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        addMachineService = null;
        addMachineData = null;
    }

    @Test
    public void createMachine() {
        addMachineService.createMachine(addMachineData);

        verify(repository, times(1)).create(machine);
    }

    @Test
    public void createMachine_withInvalidPayload_throwsException() {
        try {
            addMachineData.getMachineLocation().setName("");
            addMachineService.createMachine(addMachineData);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{AddMachineData.SHORT_MACHINE_LOCATION_NAME}, ex.getCauses());

            verify(repository, never()).create(any());
        }


    }
}