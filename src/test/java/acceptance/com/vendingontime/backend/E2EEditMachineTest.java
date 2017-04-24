package acceptance.com.vendingontime.backend;
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

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AddMachineRouter;
import com.vendingontime.backend.routes.EditMachineRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class E2EEditMachineTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;
    @Inject private AddMachineService addMachineService;

    @Inject private MachineRepository repository;
    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void editMachine() {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);
        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        Machine machine = addMachineService.createMachine(addMachineData.setRequester(supervisor));

        EditMachineData payload = FixtureFactory.generateEditMachineDataFrom(machine);
        payload.setDescription("NEW_DESCRIPTION");
        payload.setMachineState(MachineState.OUT_OF_SERVICE);
        payload.setId(null);

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .put(host + EditMachineRouter.V1_EDIT_MACHINES + machine.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.location.name", is(payload.getMachineLocation().getName()))
                .body("data.type", is(payload.getMachineType().toValue()))
                .body("data.state", is(payload.getMachineState().toValue()))
                .body("data.description", is(payload.getDescription()))
                .body("error", nullValue());

        List<Machine> machinesByCompany = repository.findMachinesByCompany(supervisor.getCompany());
        for (Machine savedMachine : machinesByCompany) {
            repository.delete(savedMachine.getId());
        }

        companyRepository.delete(supervisor.getCompany().getId());
        personRepository.delete(supervisor.getId());
    }
}
