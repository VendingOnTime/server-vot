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
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.ListTechniciansRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class E2EListTechniciansTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService logInService;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void listTechnicians_withValidToken_andSupervisorRole_returnsListOfTechnicians() {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        AddTechnicianData addTechnicianData = FixtureFactory.generateAddTechnicianData();
        addTechnicianData.setRequester(supervisor);
        Person technician = signUpService.createTechnician(addTechnicianData);

        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        given()
                .header("Authorization", "JWT " + token)
        .when()
                .get(host + ListTechniciansRouter.V1_TECHNICIANS)
        .then()
                .statusCode(HttpResponse.StatusCode.OK)
                .body("success", is(true))
                .body("data[0].id", equalTo(technician.getId()))
                .body("data[0].dni", equalTo(technician.getDni()))
                .body("data[0].email", equalTo(technician.getEmail()))
                .body("data[0].username", equalTo(technician.getUsername()))
                .body("data[0].name", equalTo(technician.getName()))
                .body("data[0].surnames", equalTo(technician.getSurnames()))
                .body("data[0].role", equalTo(PersonRole.TECHNICIAN.toString().toLowerCase()))
                .body("data[0].password", nullValue())
                .body("error", nullValue());

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void listMachines_withInvalidToken_returnsUnauthorized() {
        given()
                .header("Authorization", "JWT " + "INVALID_TOKEN")
        .when()
                .get(host + ListTechniciansRouter.V1_TECHNICIANS)
        .then()
                .statusCode(HttpResponse.StatusCode.UNAUTHORIZED);
    }

    // TODO: alberto@10/5/17 Decide if we should add acceptance tests to represent unauthorized accesses
}
