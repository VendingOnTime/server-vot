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
import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static com.vendingontime.backend.routes.AssignMaintainerRouter.MAINTAINER;
import static com.vendingontime.backend.routes.AssignMaintainerRouter.V1_MACHINES;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class E2EAssignMachineMaintainerTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;
    @Inject private AddMachineService addMachineService;

    @Inject private PersonRepository personRepository;
    @Inject private MachineRepository machineRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void assignMachineMaintainer_withValidData() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService
                .createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));
        Machine machine = addMachineService
                .createMachine(FixtureFactory.generateAddMachineData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        AssignMaintainerData payload =
                new AssignMaintainerData().setTechnicianId(technician.getId());

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .put(V1_MACHINES + machine.getId() + MAINTAINER)
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.location.name", equalTo(machine.getLocation().getName()))
                .body("data.type", is(machine.getType().toValue()))
                .body("data.state", is(machine.getState().toValue()))
                .body("data.description", equalTo(machine.getDescription()))
                .body("data.maintainer.id", equalTo(technician.getId()))
                .body("error", nullValue());

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void assignMachineMaintainer_withInvalidId_returnsNotFound() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService
                .createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        AssignMaintainerData payload =
                new AssignMaintainerData().setTechnicianId(technician.getId());

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .put(V1_MACHINES + "INVALID_ID" + MAINTAINER)
        .then()
                .statusCode(HttpResponse.StatusCode.NOT_FOUND)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", notNullValue());

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void assignMachineMaintainer_withNonExistingTechnician_returnsNotFound() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Machine machine = addMachineService
                .createMachine(FixtureFactory.generateAddMachineData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        AssignMaintainerData payload =
                new AssignMaintainerData().setTechnicianId("INVALID_ID");

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .put(V1_MACHINES + machine.getId() + MAINTAINER)
        .then()
                .statusCode(HttpResponse.StatusCode.NOT_FOUND)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", notNullValue());

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void assignMachineMaintainer_withoutPermissions_returnsBadRequest() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService
                .createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));
        Machine machine = addMachineService
                .createMachine(FixtureFactory.generateAddMachineData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        AssignMaintainerData payload =
                new AssignMaintainerData().setTechnicianId(technician.getId());

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .put(V1_MACHINES + machine.getId() + MAINTAINER)
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INSUFFICIENT_PERMISSIONS));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void assignMachineMaintainer_withInvalidPayload_returnsBadRequest() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService
                .createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));
        Machine machine = addMachineService
                .createMachine(FixtureFactory.generateAddMachineData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        given()
                .header("Authorization", "JWT " + token)
                .body("")
        .when()
                .put(V1_MACHINES + machine.getId() + MAINTAINER)
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", equalTo(MALFORMED_JSON));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void assignMachineMaintainer_withoutAuthorization_returnsUnauthorized() throws Exception {
        given()
                .header("Authorization", "JWT INVALID_TOKEN")
                .body("")
        .when()
                .put(V1_MACHINES + "SOME_ID" + MAINTAINER)
        .then()
                .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);
    }
}
