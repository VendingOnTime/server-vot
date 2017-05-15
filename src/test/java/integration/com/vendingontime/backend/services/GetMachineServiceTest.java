package integration.com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.GetMachineService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import testutils.FixtureFactory;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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

public class GetMachineServiceTest extends IntegrationTest {

    @Inject private GetMachineService service;

    @Inject private MachineRepository machineRepository;
    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void getMachineData_forValidMachineId_andAuthorizedUser() {
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Machine machine = machineRepository.create(FixtureFactory.generateMachine());

        Person savedOwner = personRepository.findById(company.getOwner().getId()).get();
        Machine savedMachine = machineRepository.findById(machine.getId()).get();

        company.addMachine(savedMachine);
        companyRepository.update(company);

        PersonRequest personRequest = FixtureFactory.generatePersonRequestFrom(machine, savedOwner);
        Machine foundMachine = service.getWith(personRequest).get();
        assertThat(savedMachine, equalTo(foundMachine));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}