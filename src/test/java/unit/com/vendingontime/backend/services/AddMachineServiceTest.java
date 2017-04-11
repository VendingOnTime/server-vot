package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.*;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

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
        repository = mock(JPAMachineRepository.class);
        companyRepository = mock(JPACompanyRepository.class);

        addMachineService = new AddMachineService(repository, companyRepository);

        addMachineData = FixtureFactory.generateAddMachineData()
                .setRequester(FixtureFactory.generateSupervisorWithCompany());

        machine = new Machine(addMachineData);
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

    @Test
    public void createMachine_withNullRequester_throwsException() {

    }
}