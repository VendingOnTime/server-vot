package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.LogInRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static com.vendingontime.backend.models.bodymodels.person.LogInData.BAD_LOGIN;
import static com.vendingontime.backend.routes.AbstractSparkRouter.MALFORMED_JSON;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

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

public class E2ELogInSupervisorTest extends E2ETest {

    @Inject private SignUpService signUpService;
    @Inject private PersonRepository repository;

    @Test
    public void logInSupervisor_withValidJSON_andValidData_returnsToken() throws Exception {
        SignUpData signUpData = FixtureFactory.generateSignUpData();

        Person supervisor = signUpService.createSupervisor(signUpData);

        LogInData payload = FixtureFactory.generateLogInDataFrom(supervisor);

        given()
            .body(payload)
        .when()
            .post(host + LogInRouter.V1_LOG_IN)
        .then()
            .statusCode(HttpResponse.StatusCode.OK)
            .body("success", equalTo(true))
            .body("data", notNullValue())
            .body("error", nullValue());

        repository.delete(supervisor.getId());
    }

    @Test
    public void logInSupervisor_withValidJSON_andInvalidData_returnsBadLogin() throws Exception {
        SignUpData signUpData = FixtureFactory.generateSignUpData();

        Person supervisor = signUpService.createSupervisor(signUpData);

        LogInData payload = FixtureFactory.generateLogInDataFrom(supervisor)
                .setEmail("another@example.com");

        given()
            .body(payload)
        .when()
            .post(host + LogInRouter.V1_LOG_IN)
        .then()
            .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
            .body("success", equalTo(false))
            .body("data", nullValue())
            .body("error", contains(BAD_LOGIN));

        repository.delete(supervisor.getId());
    }

    @Test
    public void logInSupervisor_withInvalidJSON_returnsMalformedJSON() throws Exception {
        given()
            .body("")
        .when()
            .post(host + LogInRouter.V1_LOG_IN)
        .then()
            .statusCode(HttpResponse.StatusCode.BAD_REQUEST)
            .body("success", equalTo(false))
            .body("data", nullValue())
            .body("error", is(MALFORMED_JSON));
    }

}