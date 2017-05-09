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
import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AddTechnicianRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.INVALID_EMAIL;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class E2EAddTechnicianTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;

    @Inject private PersonRepository repository;

    @Test
    public void createSupervisor_withValidJSON_andValidData_returnsSupervisorData() throws Exception {

        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        AddTechnicianData payload = FixtureFactory.generateAddTechnicianData();

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .post(host + AddTechnicianRouter.V1_ADD_TECHNICIAN)
        .then()
                .statusCode(HttpResponse.StatusCode.CREATED)
                .body("success", is(true))
                .body("data.dni", equalTo(payload.getDni()))
                .body("data.username", equalTo(payload.getUsername()))
                .body("data.email", equalTo(payload.getEmail()))
                .body("data.name", equalTo(payload.getName()))
                .body("data.surnames", equalTo(payload.getSurnames()))
                .body("data.password", nullValue())
                .body("data.role", equalTo(PersonRole.TECHNICIAN.toString().toLowerCase()))
                .body("error", nullValue());

        repository.deleteAll();
    }

    @Test
    // For a full list of all possible errors for invalid data, go to SignUpData's validate method.
    public void createSupervisor_withValidJSON_andInvalidData_returnsBadRequest() throws Exception {

        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        AddTechnicianData payload = (AddTechnicianData) FixtureFactory.generateAddTechnicianData()
                .setEmail("invalidEmail");

        given()
                .header("Authorization", "JWT " + token)
                .body(payload)
        .when()
                .post(host + AddTechnicianRouter.V1_ADD_TECHNICIAN)
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INVALID_EMAIL));

        repository.deleteAll();
    }

    @Test
    public void createSupervisor_withInvalidJSON_returnsMalformedJSON() throws Exception {

        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        given()
                .header("Authorization", "JWT " + token)
                .body("")
        .when()
                .post(host + AddTechnicianRouter.V1_ADD_TECHNICIAN)
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", is(MALFORMED_JSON));

        repository.deleteAll();
    }
}
