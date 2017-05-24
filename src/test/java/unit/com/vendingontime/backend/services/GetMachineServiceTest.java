package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.GetMachineService;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.models.bodymodels.PersonRequest.EMPTY_REQUESTER;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
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
    private AuthProvider authProvider;
    private GetMachineService service;

    private Machine machine;
    private Person owner;
    private PersonRequest personRequest;

    @Before
    public void setUp() throws Exception {
        final String COMPANY_ID = "COMPANY_ID";
        final String OWNER_ID = "OWNER_ID";

        repository = mock(MachineRepository.class);
        authProvider = mock(AuthProvider.class);
        service = new GetMachineService(repository, authProvider);

        Company company = FixtureFactory.generateCompanyWithOwner().setId(COMPANY_ID);
        owner = company.getOwner().setId(OWNER_ID);
        machine = FixtureFactory.generateMachine().setId(MACHINE_ID).setCompany(company);
        personRequest = FixtureFactory.generatePersonRequestFrom(machine, owner);

        when(repository.findById(MACHINE_ID)).thenReturn(Optional.of(machine));
        when(authProvider.canAccess(owner, machine)).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        authProvider = null;
        service = null;

        owner = null;
        machine = null;
        personRequest = null;
    }

    @Test
    public void getMachineData_forValidMachineId_andAuthorizedUser() {
        Machine foundMachine = service.getBy(personRequest).get();

        verify(repository, times(1)).findById(MACHINE_ID);
        assertEquals(machine, foundMachine);
    }

    @Test
    public void getMachineData_forInvalidMachineId() {
        personRequest.setId("INVALID_ID");
        Optional<Machine> foundMachine = service.getBy(personRequest);

        verify(repository, times(1)).findById("INVALID_ID");
        assertFalse(foundMachine.isPresent());
    }

    @Test
    public void getMachineData_forValidMachineId_andNotAuthorizedUser_throwException() {
        Person randomUser = FixtureFactory.generateCustomer().setId("RANDOM_USER");

        when(authProvider.canAccess(randomUser, machine)).thenReturn(false);
        personRequest.setRequester(randomUser);
        try {
            service.getBy(personRequest);
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{INSUFFICIENT_PERMISSIONS}, ex.getCauses());
            verify(repository, times(1)).findById(MACHINE_ID);
        }
    }

    @Test
    public void getMachineData_forNullPerson_throwException() {
        personRequest.setRequester(null);
        try {
            service.getBy(personRequest);
            fail();
        } catch (BusinessLogicException ex) {
            assertArrayEquals(new String[]{EMPTY_REQUESTER}, ex.getCauses());
            verify(repository, never()).findById(MACHINE_ID);
        }
    }
}