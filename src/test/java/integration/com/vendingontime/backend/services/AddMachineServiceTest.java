package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.JPACompanyRepository;
import com.vendingontime.backend.repositories.JPAMachineRepository;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.services.AddMachineService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

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

public class AddMachineServiceTest extends IntegrationTest {
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String LOCATION_NAME = "LOCATION_NAME";
    private static final MachineLocation MACHINE_LOCATION = new MachineLocation().setName(LOCATION_NAME);

    @Inject private JPAMachineRepository repository;
    @Inject private JPACompanyRepository companyRepository;
    @Inject private JPAPersonRepository personRepository;
    @Inject private AddMachineService service;

    private AddMachineData addMachineData;

    @Before
    public void setUp() throws Exception {
        addMachineData = FixtureFactory.generateAddMachineData();
    }

    @After
    public void tearDown() throws Exception {
        addMachineData = null;
    }

    @Test
    public void createMachine() {
        Person owner = personRepository.create(FixtureFactory.generateSupervisor());

        Company company = companyRepository.create(FixtureFactory.generateCompany());

        Person savedOwner = personRepository.findById(owner.getId()).get();

        company.setOwner(savedOwner);
        companyRepository.update(company);

        Machine machine = service.createMachine(addMachineData.setRequester(savedOwner));

        assertNotNull(machine);

        Machine savedMachine = repository.findById(machine.getId()).get();
        Company savedCompany = companyRepository.findById(company.getId()).get();

        assertNotNull(savedMachine.getId());
        assertEquals(1, savedCompany.getMachines().size());

        repository.delete(savedMachine.getId());
        personRepository.delete(savedOwner.getId());
    }
}