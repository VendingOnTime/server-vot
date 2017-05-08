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
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AddMachineRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static com.vendingontime.backend.models.bodymodels.machine.AddMachineData.INVALID_DESCRIPTION;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static io.restassured.RestAssured.given;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class E2EAddMachineTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;
    @Inject private MachineRepository machineRepository;
    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void addMachine_withValidData() {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);
        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        AddMachineData payload = FixtureFactory.generateAddMachineData();

        given()
            .header("Authorization", "JWT " + token)
            .body(payload)
        .when()
            .post(host + AddMachineRouter.V1_MACHINES)
        .then()
            .statusCode(HttpResponse.StatusCode.CREATED)
            .body("success", is(true))
            .body("data.id", notNullValue())
            .body("data.location.name", is(payload.getLocation().getName()))
            .body("data.type", is(payload.getType().toValue()))
            .body("data.state", is(payload.getState().toValue()))
            .body("data.description", is(payload.getDescription()))
            .body("error", nullValue());

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    // For a full list of all possible errors for invalid data, go to AddMachineData's validate method.
    public void addMachine_withInvalidData() {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);
        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        AddMachineData payload = FixtureFactory.generateAddMachineData();
        payload.setDescription(null);

        given()
            .header("Authorization", "JWT " + token)
            .body(payload)
        .when()
            .post(host + AddMachineRouter.V1_MACHINES)
        .then()
            .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
            .body("success", is(false))
            .body("data", nullValue())
            .body("error", contains(INVALID_DESCRIPTION));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    // This will also happen when the JSON has more fields than required.
    public void addMachine_withInvalidJSON() {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);
        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        given()
            .header("Authorization", "JWT " + token)
            .body("")
        .when()
            .post(host + AddMachineRouter.V1_MACHINES)
        .then()
            .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
            .body("success", is(false))
            .body("data", nullValue())
            .body("error", is(MALFORMED_JSON));

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void addMachine_withInvalidToken() {
        given()
            .header("Authorization", "JWT " + "INVALID_TOKEN")
            .body("")
        .when()
            .post(host + AddMachineRouter.V1_MACHINES)
        .then()
            .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);
    }
}
