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
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.EditPersonRouter;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.INVALID_EMAIL;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class E2EEditSupervisorDataTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService loginService;

    @Inject private PersonRepository repository;

    @Test
    public void editSupervisor_withValidJSON_andValidData_returnsUpdatedSupervisorData() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(supervisor);

        payload.setName("NEW_NAME");

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.dni", equalTo(supervisor.getDni()))
                .body("data.username", equalTo(supervisor.getUsername()))
                .body("data.email", equalTo(supervisor.getEmail()))
                .body("data.name", equalTo(payload.getName()))
                .body("data.surnames", equalTo(supervisor.getSurnames()))
                .body("data.password", nullValue())
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toString().toLowerCase()))
                .body("error", nullValue());

        repository.delete(supervisor.getId());
    }

    @Test
    //It also throws SignUpData's validation errors
    public void editSupervisor_withValidJSON_andInvalidData_returnsBadRequest() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(supervisor);
        payload.setEmail("invalidEmail");

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INVALID_EMAIL));

        repository.deleteAll();
    }

    @Test
    public void editSupervisor_withInvalidJSON_returnsMalformedJSON() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        given()
                .body("")
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", is(MALFORMED_JSON));

        repository.deleteAll();
    }

    @Test
    public void editSupervisor_withUnauthorizedUser_returnsInsufficientPermissions() throws Exception {
        Person supervisor1 = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person supervisor2 = signUpService.createSupervisor(FixtureFactory.generateSignUpData()
                .setDni("12345678W").setEmail("another.email@example.com").setUsername("supervisor2"));

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor2));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(supervisor1);

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + supervisor1.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INSUFFICIENT_PERMISSIONS));

        repository.deleteAll();
    }
}
