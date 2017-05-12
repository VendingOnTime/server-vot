package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AssignMaintainerRouter;
import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;



import static org.hamcrest.core.Is.is;
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

public class AssignMaintainerRouterTest extends IntegrationTest {

    @Inject
    private SignUpService signUpService;
    @Inject private AddMachineService addMachineService;
    @Inject private AssignMaintainerRouter assignMaintainerRouter;

    @Inject private PersonRepository personRepository;
    @Inject private MachineRepository machineRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void assignMachineMaintainer() throws Exception {
        Person requester = signUpService
                .createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService
                .createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));
        Machine machine = addMachineService
                .createMachine(FixtureFactory.generateAddMachineData().setRequester(requester));

        ObjectMapper mapper = new ObjectMapper();
        String stringifiedAssignment = mapper
                .writeValueAsString(new AssignMaintainerData().setTechnicianId(technician.getId()));

        String result = (String) assignMaintainerRouter.assignMachineMaintainer(machine.getId(), stringifiedAssignment, requester)
                .handle(mock(Request.class), mock(Response.class));

        RESTResult restResult = mapper.readValue(result, RESTResult.class);

        assertThat(restResult.getSuccess(), is(true));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}