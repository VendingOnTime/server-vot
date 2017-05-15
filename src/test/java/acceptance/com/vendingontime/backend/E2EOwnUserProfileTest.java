package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.GetPersonProfileRouter;
import com.vendingontime.backend.routes.UserProfileRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

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

public class E2EOwnUserProfileTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;
    @Inject private PersonRepository repository;

    @Test
    public void retrieveUserProfile_supervisor_withValidToken_returnsUserData() throws Exception {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);

        LogInData logInData = FixtureFactory.generateLogInDataFrom(supervisor);
        String token = logInService.authorizeUser(logInData);

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + UserProfileRouter.V1_PROFILE)
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.dni", equalTo(supervisor.getDni()))
                .body("data.username", equalTo(supervisor.getUsername()))
                .body("data.password", nullValue())
                .body("data.email", equalTo(supervisor.getEmail()))
                .body("data.name", equalTo(supervisor.getName()))
                .body("data.surnames", equalTo(supervisor.getSurnames()))
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toValue()))
                .body("error", nullValue());

        repository.deleteAll();
    }

    @Test
    public void retrieveUserProfile_supervisor_withValidTokenAndId_returnsUserData() throws Exception {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);

        LogInData logInData = FixtureFactory.generateLogInDataFrom(supervisor);
        String token = logInService.authorizeUser(logInData);

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + GetPersonProfileRouter.V1_USERS_PROFILE + supervisor.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.dni", equalTo(supervisor.getDni()))
                .body("data.username", equalTo(supervisor.getUsername()))
                .body("data.password", nullValue())
                .body("data.email", equalTo(supervisor.getEmail()))
                .body("data.name", equalTo(supervisor.getName()))
                .body("data.surnames", equalTo(supervisor.getSurnames()))
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toValue()))
                .body("error", nullValue());

        repository.deleteAll();
    }

    @Test
    public void retrieveUserProfile_technician_withValidToken_returnsUserData() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + UserProfileRouter.V1_PROFILE)
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.dni", equalTo(technician.getDni()))
                .body("data.username", equalTo(technician.getUsername()))
                .body("data.password", nullValue())
                .body("data.email", equalTo(technician.getEmail()))
                .body("data.name", equalTo(technician.getName()))
                .body("data.surnames", equalTo(technician.getSurnames()))
                .body("data.role", equalTo(PersonRole.TECHNICIAN.toValue()))
                .body("error", nullValue());

        repository.deleteAll();
    }

    @Test
    public void retrieveUserProfile_technician_withValidTokenAndId_returnsUserData() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + GetPersonProfileRouter.V1_USERS_PROFILE + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.dni", equalTo(technician.getDni()))
                .body("data.username", equalTo(technician.getUsername()))
                .body("data.password", nullValue())
                .body("data.email", equalTo(technician.getEmail()))
                .body("data.name", equalTo(technician.getName()))
                .body("data.surnames", equalTo(technician.getSurnames()))
                .body("data.role", equalTo(PersonRole.TECHNICIAN.toValue()))
                .body("error", nullValue());

        repository.deleteAll();
    }

    @Test
    public void retrieveUserProfile_withInvalidToken_returnsUnauthorized() throws Exception {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        signUpService.createSupervisor(signUpData);

        given()
            .header("Authorization", "JWT " + "INVALID_TOKEN")
        .when()
            .get(host + UserProfileRouter.V1_PROFILE)
        .then()
            .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);

        repository.deleteAll();
    }
}