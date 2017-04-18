package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
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

    @Inject private JPAMachineRepository machineRepository;
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
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Person savedOwner = personRepository.findById(company.getOwner().getId()).get();

        addMachineData.setRequester(savedOwner);
        Machine machine = service.createMachine(addMachineData);

        assertNotNull(machine);

        Machine savedMachine = machineRepository.findById(machine.getId()).get();
        Company savedCompany = companyRepository.findById(company.getId()).get();

        assertNotNull(savedMachine.getId());
        assertEquals(1, savedCompany.getMachines().size());

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}