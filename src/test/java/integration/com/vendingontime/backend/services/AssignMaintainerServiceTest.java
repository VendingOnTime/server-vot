package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.AssignMaintainerService;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

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

public class AssignMaintainerServiceTest extends IntegrationTest {

    @Inject private SignUpService signUpService;
    @Inject private AddMachineService addMachineService;
    @Inject private AssignMaintainerService assignMaintainerService;

    @Inject private PersonRepository personRepository;
    @Inject private MachineRepository machineRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void assignMaintainer() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        AddTechnicianData addTechnicianData = FixtureFactory.generateAddTechnicianData();
        addTechnicianData.setRequester(requester);
        Person technician = signUpService.createTechnician(addTechnicianData);

        AddMachineData machineCandidate = FixtureFactory.generateAddMachineData();
        machineCandidate.setRequester(requester);
        Machine machine = addMachineService.createMachine(machineCandidate);

        AssignMaintainerData assignMaintainerData = new AssignMaintainerData()
                .setId(machine.getId())
                .setTechnicianId(technician.getId())
                .setRequester(requester);

        Optional<Machine> updatedMachine = assignMaintainerService.assignMaintainer(assignMaintainerData);

        assertThat(updatedMachine.isPresent(), is(true));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}