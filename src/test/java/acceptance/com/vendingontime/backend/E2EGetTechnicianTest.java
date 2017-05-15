package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.GetPersonProfileRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

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
public class E2EGetTechnicianTest extends E2ETest {
    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void getMachine_withValidId_andValidAuthorization_returnsMachineData() {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));
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
                .body("data.email", equalTo(technician.getEmail()))
                .body("data.name", equalTo(technician.getName()))
                .body("data.surnames", equalTo(technician.getSurnames()))
                .body("data.role", equalTo(PersonRole.TECHNICIAN.toValue()))
                .body("error", nullValue());

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getMachine_withValidId_andInvalidAuthorization_returnsUnauthorized() {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));
        given()
                .header("Authorization", "JWT " + "INVALID_TOKEN")
        .when()
                .get(host + GetPersonProfileRouter.V1_USERS_PROFILE + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getMachine_withInvalidId_andValidAuthorization_returnsNotFound() {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));
        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + GetPersonProfileRouter.V1_USERS_PROFILE + "INVALID_ID")
        .then()
                .statusCode(HttpResponse.StatusCode.NOT_FOUND);

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void getMachine_withValidId_andValidAuthorization_butDifferentCompany_returnsUnauthorized() {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        SignUpData supervisorCandidate = FixtureFactory.generateSignUpData()
                .setDni("22222222A")
                .setEmail("another@example.com")
                .setUsername("anotherUsername");
        Person anotherSupervisor = signUpService.createSupervisor(supervisorCandidate);

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(anotherSupervisor));
        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + GetPersonProfileRouter.V1_USERS_PROFILE + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INSUFFICIENT_PERMISSIONS));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}
