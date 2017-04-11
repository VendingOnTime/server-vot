package integration.com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.ListMachinesService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

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

public class ListMachinesServiceTest extends IntegrationTest {

    @Inject
    private ListMachinesService service;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private MachineRepository machineRepository;

    @Test
    public void listFor_owner() throws Exception {
        Person owner = personRepository.create(new Person().setRole(PersonRole.SUPERVISOR));

        Company company = companyRepository.create(new Company());
        Machine machine1 = machineRepository.create(new Machine());
        Machine machine2 = machineRepository.create(new Machine());

        Person savedOwner = personRepository.findById(owner.getId()).get();
        Machine savedMachine1 = machineRepository.findById(machine1.getId()).get();
        Machine savedMachine2 = machineRepository.findById(machine2.getId()).get();

        company.setOwner(savedOwner);
        company.addMachine(savedMachine1);
        company.addMachine(savedMachine2);
        companyRepository.update(company);

        List<Machine> machines = service.listFor(savedOwner);
        assertThat(new HashSet<>(machines), equalTo(company.getMachines()));
    }

}