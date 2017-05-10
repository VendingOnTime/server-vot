package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.GetMachineRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

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

public class E2EGetMachineTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;
    @Inject private AddMachineService addMachineService;

    @Inject private MachineRepository machineRepository;
    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void getMachine_withValidId_andValidAuthorization_returnsMachineData() {
        final SignUpData signUpData = FixtureFactory.generateSignUpData();
        final Person supervisor = signUpService.createSupervisor(signUpData);
        final String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        final AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(supervisor);
        final Machine machine = addMachineService.createMachine(addMachineData);

        final List<Machine> machinesByCompany = machineRepository.findMachinesByCompany(supervisor.getOwnedCompany());
        final String savedMachineId = machinesByCompany.get(0).getId();

        given()
            .header("Authorization", "JWT " + token)
        .when()
            .get(host + GetMachineRouter.V1_MACHINES + savedMachineId)
        .then()
            .statusCode(HttpResponse.StatusCode.OK)
            .body("success", is(true))
            .body("data.id", equalTo(machine.getId()))
            .body("data.location.name", equalTo(machine.getLocation().getName()))
            .body("data.type", equalTo(machine.getType().toValue()))
            .body("data.state", equalTo(machine.getState().toValue()))
            .body("data.description", equalTo(machine.getDescription()))
            .body("error", nullValue());

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getMachine_withValidId_andInvalidAuthorization_returnsUnauthorized() {
        final SignUpData signUpData = FixtureFactory.generateSignUpData();
        final Person supervisor = signUpService.createSupervisor(signUpData);

        final AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(supervisor);
        addMachineService.createMachine(addMachineData);

        final List<Machine> machinesByCompany = machineRepository.findMachinesByCompany(supervisor.getOwnedCompany());
        final String savedMachineId = machinesByCompany.get(0).getId();

        given()
            .header("Authorization", "JWT " + "INVALID_TOKEN")
        .when()
            .get(host + GetMachineRouter.V1_MACHINES + savedMachineId)
        .then()
            .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getMachine_withInvalidId_andValidAuthorization_returnsNotFound() {
        final SignUpData signUpData = FixtureFactory.generateSignUpData();
        final Person supervisor = signUpService.createSupervisor(signUpData);
        final String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        given()
            .header("Authorization", "JWT " + token)
        .when()
            .get(host + GetMachineRouter.V1_MACHINES + "INVALID_ID")
        .then()
            .statusCode(HttpResponse.StatusCode.NOT_FOUND);

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    //TODO miguel@30/04/2017 Should be 'unauthorized' instead of 'bad request'?
    public void getMachine_withValidId_andValidAuthorization_butDifferentCompany_returnsUnauthorized() {
        final SignUpData signUpData = FixtureFactory.generateSignUpData();
        final Person supervisor = signUpService.createSupervisor(signUpData);

        final AddMachineData addMachineData = FixtureFactory.generateAddMachineData();
        addMachineData.setRequester(supervisor);
        addMachineService.createMachine(addMachineData);

        final SignUpData anotherSignUpData = FixtureFactory.generateSignUpData()
                .setDni("22222222A")
                .setEmail("another@example.com")
                .setUsername("anotherUsername");
        final Person anotherSupervisor = signUpService.createSupervisor(anotherSignUpData);
        final String anotherToken = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(anotherSupervisor));

        final List<Machine> machinesByCompany = machineRepository.findMachinesByCompany(supervisor.getOwnedCompany());
        final String savedMachineId = machinesByCompany.get(0).getId();

        given()
            .header("Authorization", "JWT " + anotherToken)
        .when()
            .get(host + GetMachineRouter.V1_MACHINES + savedMachineId)
        .then()
            .statusCode(HttpResponse.StatusCode.BAD_REQUEST);

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}