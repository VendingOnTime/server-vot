package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.GetMachineService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.junit.Assert.*;
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

public class GetMachineServiceTest {
    private final String MACHINE_ID = "MACHINE_ID";

    private MachineRepository repository;
    private GetMachineService service;

    private Machine machine;
    private Person owner;

    @Before
    public void setUp() throws Exception {
        final String COMPANY_ID = "COMPANY_ID";
        final String OWNER_ID = "OWNER_ID";

        repository = mock(MachineRepository.class);
        service = new GetMachineService(repository);

        Company company = FixtureFactory.generateCompanyWithOwner().setId(COMPANY_ID);
        owner = company.getOwner().setId(OWNER_ID);
        machine = FixtureFactory.generateMachine().setId(MACHINE_ID).setCompany(company);

        when(repository.findById(MACHINE_ID)).thenReturn(Optional.of(machine));
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        service = null;

        owner = null;
        machine = null;
    }

    @Test
    public void getMachineData_forValidMachineId_andAuthorizedUser() {
        Machine foundMachine = service.getDataFrom(MACHINE_ID, owner).get();

        verify(repository, times(1)).findById(MACHINE_ID);
        assertEquals(machine, foundMachine);
    }

    @Test
    public void getMachineData_forInvalidMachineId() {
        Optional<Machine> foundMachine = service.getDataFrom("INVALID_ID", owner);

        verify(repository, times(1)).findById("INVALID_ID");
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forValidMachineId_andNotAuthorizedUser() {
        Company randomCompany = FixtureFactory.generateCompanyWithOwner().setId("RANDOM_COMPANY");
        Person randomUser = randomCompany.getOwner().setId("RANDOM_USER");

        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, randomUser);

        verify(repository, times(1)).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forNullPerson() {
        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, null);

        verify(repository, never()).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forPersonWithNullId() {
        Person invalidUser = FixtureFactory.generateSupervisor().setId(null);
        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, invalidUser);

        verify(repository, never()).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forPersonWithEmptyId() {
        Person invalidUser = FixtureFactory.generateSupervisor().setId("");
        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, invalidUser);

        verify(repository, never()).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forPersonWithNullCompany() {
        Person invalidUser = FixtureFactory.generateSupervisor()
                .setId("INVALID_USER")
                .setCompany(null);
        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, invalidUser);

        verify(repository, never()).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forPersonWithNullCompanyId() {
        Person invalidUser = FixtureFactory.generateSupervisor()
                .setId("INVALID_USER")
                .setCompany(FixtureFactory.generateCompany().setId(null));
        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, invalidUser);

        verify(repository, never()).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forPersonWithEmptyCompanyId() {
        Person invalidUser = FixtureFactory.generateSupervisor()
                .setId("INVALID_USER")
                .setCompany(FixtureFactory.generateCompany().setId(""));
        Optional<Machine> foundMachine = service.getDataFrom(MACHINE_ID, invalidUser);

        verify(repository, never()).findById(MACHINE_ID);
        assertFalse(foundMachine.isPresent());
    }
}