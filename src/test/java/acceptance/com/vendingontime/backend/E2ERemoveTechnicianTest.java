package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.RemoveMachineRouter;
import com.vendingontime.backend.routes.RemoveTechnicianRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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
public class E2ERemoveTechnicianTest extends E2ETest {
    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void removeTechnician_withValidId_andValidToken_returnsDeletedMachineData() {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .delete(host + RemoveTechnicianRouter.V1_REMOVE_TECHNICIAN+ technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data.dni", equalTo(technician.getDni()))
                .body("data.username", equalTo(technician.getUsername()))
                .body("data.email", equalTo(technician.getEmail()))
                .body("data.name", equalTo(technician.getName()))
                .body("data.surnames", equalTo(technician.getSurnames()))
                .body("data.password", nullValue())
                .body("data.role", equalTo(PersonRole.TECHNICIAN.toValue()))
                .body("error", nullValue());

        Optional<Person> byId = personRepository.findById(technician.getId());
        assertThat(byId.isPresent(), is(false));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void removeTechnician_withValidId_andInvalidToken_returnsUnauthorized() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        given()
                .header("Authorization", "JWT " + "INVALID_TOKEN")
        .when()
                .delete(host + RemoveTechnicianRouter.V1_REMOVE_TECHNICIAN + technician.getId())
        .then()
                .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);

        Optional<Person> byId = personRepository.findById(technician.getId());
        assertThat(byId.isPresent(), is(true));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void removeTechnician_onInsurrection_getsAvoided() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(technician));

        given() // an insurgent
                .header("Authorization", "JWT " + token)
        .when() // performs insurrection
                .delete(host + RemoveTechnicianRouter.V1_REMOVE_TECHNICIAN+ requester.getId())
        .then() // gets a facepalm
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INSUFFICIENT_PERMISSIONS));

        Optional<Person> byId = personRepository.findById(requester.getId());
        assertThat(byId.isPresent(), is(true));

        personRepository.deleteAll();
        companyRepository.deleteAll();
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

        given() // a competitor
                .header("Authorization", "JWT " + token)
        .when() // performs unfair competition
                .delete(host + RemoveTechnicianRouter.V1_REMOVE_TECHNICIAN+ supervisor.getId())
        .then() // covert operation cancelled
                .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
                .body("success", is(false))
                .body("data", nullValue())
                .body("error", contains(INSUFFICIENT_PERMISSIONS));

        Optional<Person> byId = personRepository.findById(technician.getId());
        assertThat(byId.isPresent(), is(true));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void removeTechnician_withInvalidId_andValidToken_returnsNotFound() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(requester));

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .delete(host + RemoveMachineRouter.V1_REMOVE_MACHINE + "INVALID_ID")
        .then()
                .statusCode(HttpResponse.StatusCode.NOT_FOUND);

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}
