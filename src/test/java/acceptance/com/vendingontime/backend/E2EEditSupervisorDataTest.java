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
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.hamcrest.Matchers;
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
    @Inject private LogInService logInService;

    @Inject private PersonRepository repository;

    @Test
    public void supervisorEditTechnician_withValidJSON_andValidData_returnsUpdatedTechnicianData() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(technician);
        payload.setName("NEW_NAME");

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
    public void removeTechnician_differentCompanies_outOfMyProperty() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        SignUpData anotherSupervisorData = FixtureFactory.generateSignUpData()
                .setUsername("ANOTHER_USERNAME")
                .setEmail("another.email@example.com")
                .setDni("22222222Z");
        Person requester = signUpService.createSupervisor(anotherSupervisorData);

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        EditPersonData payload = FixtureFactory.generateEditPersonDataFrom(technician);
        payload.setName("NEW_NAME");

        given() // a competitor
                .body(payload)
                .header("Authorization", "JWT " + token)
        .when() // performs unfair competition
                .put(host + EditPersonRouter.V1_EDIT_PROFILE + technician.getId())
        .then() // covert operation cancelled
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", Matchers.nullValue())
                .body("error", Matchers.contains(INSUFFICIENT_PERMISSIONS));

        repository.deleteAll();
    }

    @Test
    //It also throws SignUpData's validation errors
    public void supervisorEditTechnician_withValidJSON_andInvalidData_returnsBadRequest() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

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
    public void supervisorEditTechnician_withInvalidJSON_returnsMalformedJSON() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

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
