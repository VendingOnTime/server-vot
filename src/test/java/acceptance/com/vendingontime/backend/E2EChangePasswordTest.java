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
import com.vendingontime.backend.models.bodymodels.person.EditPasswordData;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.EditPasswordRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class E2EChangePasswordTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private LogInService loginService;

    @Inject private PersonRepository repository;

    @Test
    public void changeSupervisorPassword_withValidJSON_andValidData_returnsSupervisorData() throws Exception {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());

        String token = loginService.authorizeUser(FixtureFactory.generateLogInDataFrom(supervisor));

        EditPasswordData payload = FixtureFactory.generateEditPasswordDataFrom(supervisor);

        String newPassword = "NEW_PASSWORD";
        payload.setNewPassword(newPassword);
        payload.setId(null);

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

        // FIXME: alberto@7/5/17 This does not work because supervisor object is cached
        //                       tested manually password is changed correctly

        // loginService.authorizeUser(new LogInData().setEmail(supervisor.getEmail()).setPassword(newPassword));

        repository.delete(supervisor.getId());
    }
}
