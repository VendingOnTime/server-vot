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
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.ListMachinesRouter;
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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class E2EListMachinesTest extends E2ETest {
    @Inject
    private SignUpService signUpService;

    @Inject
    private LogInService logInService;

    @Inject
    private AddMachineService addMachineService;

    @Inject
    private MachineRepository repository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Test
    public void listMachine() {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);
        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(supervisor);
        Machine machine = addMachineService.createMachine(addMachineData);

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + ListMachinesRouter.V1_MACHINES)
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data[0].id", equalTo(machine.getId()))
                .body("data[0].location.name", equalTo(machine.getLocation().getName()))
                .body("data[0].type", equalTo(machine.getType().toValue()))
                .body("data[0].state", equalTo(machine.getState().toValue()))
                .body("data[0].description", equalTo(machine.getDescription()))
                .body("error", nullValue());

        List<Machine> machinesByCompany = repository.findMachinesByCompany(supervisor.getCompany());
        for (Machine companyMachine : machinesByCompany) {
            repository.delete(companyMachine.getId());
        }

        companyRepository.delete(supervisor.getCompany().getId());
        personRepository.delete(supervisor.getId());
    }
}
