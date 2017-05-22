package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.EditMachineService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

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
public class EditMachineServiceTest extends IntegrationTest {

    @Inject private AddMachineService addMachineService;
    @Inject private EditMachineService editMachineService;

    @Inject private MachineRepository machineRepository;
    @Inject private CompanyRepository companyRepository;
    @Inject private PersonRepository personRepository;

    @Test
    public void editMachine() {
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Person savedOwner = personRepository.findById(company.getOwner().getId()).get();

        AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(savedOwner);
        Machine machine = addMachineService.createMachine(addMachineData);

        EditMachineData editMachineData = FixtureFactory.generateEditMachineDataFrom(machine);
        editMachineData.setRequester(savedOwner);

        String newDescription = "NEW_DESCRIPTION";
        editMachineData.setDescription(newDescription);
        Optional<Machine> possibleUpdatedMachine = editMachineService.updateMachine(editMachineData);

        assertThat(possibleUpdatedMachine.isPresent(), is(true));
        assertThat(possibleUpdatedMachine.get().getDescription(), equalTo(newDescription));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}