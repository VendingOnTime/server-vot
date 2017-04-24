package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AddMachineRouter;
import com.vendingontime.backend.routes.EditMachineRouter;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.services.AddMachineService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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

public class EditMachineRouterTest extends IntegrationTest {

    @Inject private EditMachineRouter router;

    @Inject private AddMachineService addMachineService;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;
    @Inject private MachineRepository machineRepository;

    @Test
    public void addMachine() throws Exception {
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Person savedOwner = personRepository.findById(company.getOwner().getId()).get();

        AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(savedOwner);
        Machine machine = addMachineService.createMachine(addMachineData);

        ObjectMapper mapper = new ObjectMapper();
        EditMachineData editMachineData = FixtureFactory.generateEditMachineDataFrom(machine);
        editMachineData.setId(null);
        editMachineData.setDescription("NEW_DESCRIPTION");
        String stringifiedMachine = mapper.writeValueAsString(editMachineData);

        AppRoute post = router.updateMachine(machine.getId(), stringifiedMachine, savedOwner);
        String result = (String) post.handle(mock(Request.class), mock(Response.class));

        RESTResult restResult = mapper.readValue(result, RESTResult.class);
        assertTrue(restResult.getSuccess());

        Machine savedMachine = machineRepository.findById(machine.getId()).get();
        Company savedCompany = companyRepository.findById(company.getId()).get();

        // TODO: 23/4/17 Replace with deleteAll methods
        machineRepository.delete(savedMachine.getId());
        personRepository.delete(savedOwner.getId());
        companyRepository.delete(savedCompany.getId());
    }
}