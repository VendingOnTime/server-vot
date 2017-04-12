package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.ListMachinesService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
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

public class ListMachinesServiceTest {
    private MachineRepository repository;
    private ListMachinesService listMachinesService;

    private Person owner;
    private Person customer;
    private Company company;

    @Before
    public void setUp() throws Exception {
        repository = mock(MachineRepository.class);
        listMachinesService = new ListMachinesService(repository);

        company = FixtureFactory.generateCompanyWithOwner();
        owner = company.getOwner();
        customer = FixtureFactory.generateCustomer();

    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        listMachinesService = null;
        owner = null;
        customer = null;
        company = null;
    }

    @Test
    public void listFor_ownerWithMachines_returnsListOfMachines() throws Exception {
        company.addMachine(new Machine().setId("MACHINE_1"));
        company.addMachine(new Machine().setId("MACHINE_2"));

        when(repository.findMachinesByCompany(company)).thenReturn(new LinkedList<>(company.getMachines()));

        List<Machine> machines = listMachinesService.listFor(owner);
        assertThat(machines, equalTo(new LinkedList<>(company.getMachines())));
    }

    @Test
    public void listFor_ownerWithNoMachines_returnsEmptyList() throws Exception {
        when(repository.findMachinesByCompany(company)).thenReturn(new LinkedList<>(company.getMachines()));

        List<Machine> machines = listMachinesService.listFor(owner);
        assertThat(machines.size(), is(0));
    }

    @Test
    public void listFor_customer_returnsInsufficientPermissions() throws Exception {
        try {
            listMachinesService.listFor(customer);
            fail();
        } catch (BusinessLogicException e) {
            verify(repository, never()).findMachinesByCompany(any());
            assertThat(e.getCauses(), equalTo(new String[]{ListMachinesService.INSUFFICIENT_PERMISSIONS}));
        }
    }

    @Test(expected = NullPointerException.class)
    public void listFor_nullPerson_throwsException() throws Exception {
        listMachinesService.listFor(null);
    }

    @Test(expected = NullPointerException.class)
    public void listFor_unknownRole_throwsException() throws Exception {
        listMachinesService.listFor(new Person());
    }
}