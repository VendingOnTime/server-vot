package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.person.EditPasswordData;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.EditPasswordRouter;
import com.vendingontime.backend.routes.LogInRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static com.vendingontime.backend.models.bodymodels.person.EditPasswordData.EMPTY_NEW_PASSWORD;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

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
public class E2EChangePasswordTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService loginService;

    @Inject private PersonRepository personRepository;

    @Test
    public void changeSupervisorPassword_withValidJSON_andValidData_returnsSupervisorData() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        EditPasswordData payload = FixtureFactory.generateEditPasswordDataFrom(supervisor);

        String newPassword = "NEW_PASSWORD";
        payload.setNewPassword(newPassword);

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPasswordRouter.V1_EDIT_PASSWORD + supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.dni", equalTo(supervisor.getDni()))
                .body("data.username", equalTo(supervisor.getUsername()))
                .body("data.email", equalTo(supervisor.getEmail()))
                .body("data.name", equalTo(supervisor.getName()))
                .body("data.surnames", equalTo(supervisor.getSurnames()))
                .body("data.password", nullValue())
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toString().toLowerCase()))
                .body("error", nullValue());

        LogInData newLogInData = new LogInData().setEmail(supervisor.getEmail()).setPassword(newPassword);
        given()
                .body(newLogInData)
        .when()
                .post(host + LogInRouter.V1_LOG_IN)
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true));

        personRepository.delete(supervisor.getId());
    }

    @Test
    // For a full list of all possible errors for invalid data, go to EditPasswordData's validate method.
    public void changeSupervisorPassword_withValidJSON_andInvalidData_returnsBadRequest() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        EditPasswordData payload = FixtureFactory.generateEditPasswordDataFrom(supervisor);

        payload.setNewPassword("");

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPasswordRouter.V1_EDIT_PASSWORD + supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(EMPTY_NEW_PASSWORD));

        personRepository.delete(supervisor.getId());
    }

    @Test
    public void editSupervisor_withInvalidJSON_returnsMalformedJSON() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        given()
                .body("")
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPasswordRouter.V1_EDIT_PASSWORD+ supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", is(MALFORMED_JSON));

        personRepository.deleteAll();
    }

    @Test
    public void editSupervisor_withUnauthorizedUser_returnsInsufficientPermissions() throws Exception {
        Person supervisor1 = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person supervisor2 = signUpService.createSupervisor(FixtureFactory.generateSignUpData()
                .setDni("12345678W").setEmail("another.email@example.com").setUsername("supervisor2"));

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor2));

        EditPasswordData payload = FixtureFactory.generateEditPasswordDataFrom(supervisor1);

        String newPassword = "NEW_PASSWORD";
        payload.setNewPassword(newPassword);

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPasswordRouter.V1_EDIT_PASSWORD+ supervisor1.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INSUFFICIENT_PERMISSIONS));

        personRepository.deleteAll();
    }
}
