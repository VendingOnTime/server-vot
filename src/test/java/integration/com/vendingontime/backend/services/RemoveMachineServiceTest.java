package integration.com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.RemoveMachineService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
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

public class RemoveMachineServiceTest extends IntegrationTest{
    @Inject private AddMachineService addMachineService;
    @Inject private RemoveMachineService removeMachineService;

    @Inject private MachineRepository machineRepository;
    @Inject private CompanyRepository companyRepository;
    @Inject private PersonRepository personRepository;

    @Test
    public void removeMachine() {
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Person requester = personRepository.findById(company.getOwner().getId()).get();

        AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(requester);
        Machine machine = addMachineService.createMachine(addMachineData);

        PersonRequest personRequest = FixtureFactory.generatePersonRequestFrom(machine, requester);
        Optional<Machine> possibleRemovedMachine = removeMachineService.removeBy(personRequest);

        assertThat(possibleRemovedMachine.isPresent(), is(true));
        assertThat(possibleRemovedMachine.get().isDisabled(), is(true));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}