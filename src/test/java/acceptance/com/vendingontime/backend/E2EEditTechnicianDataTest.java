package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.EditPersonRouter;
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

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class E2EEditTechnicianDataTest extends E2ETest {
    @Inject private SignUpService signUpService;
    @Inject private LogInService loginService;

    @Inject private PersonRepository repository;

    @Test
    public void editTechnician_withValidJSON_andValidData_returnsUpdatedSupervisorData() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(technician);
        payload.setName("NEW_NAME");
        payload.setRole(PersonRole.SUPERVISOR);

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.dni", equalTo(technician.getDni()))
                .body("data.username", equalTo(technician.getUsername()))
                .body("data.email", equalTo(technician.getEmail()))
                .body("data.name", equalTo(payload.getName()))
                .body("data.surnames", equalTo(technician.getSurnames()))
                .body("data.password", nullValue())
                .body("data.role", equalTo(PersonRole.TECHNICIAN.toValue()))
                .body("error", nullValue());

        repository.deleteAll();
    }

    @Test
    //It also throws SignUpData's validation errors
    public void editTechnician_withValidJSON_andInvalidData_returnsBadRequest() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(technician);
        payload.setEmail("invalidEmail");

        given()
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INVALID_EMAIL));

        repository.deleteAll();
    }

    @Test
    public void editTechnician_withInvalidJSON_returnsMalformedJSON() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        given()
                .body("")
                .header("Authorization", "JWT " + token)
        .when()
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", is(MALFORMED_JSON));

        repository.deleteAll();
    }
}
