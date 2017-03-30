package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static java.util.Objects.isNull;
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
public class E2ESignUpSupervisorTest extends E2ETest {

    private static final String DNI = "12345678B";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "username@test.com";
    private static final String NAME = "NAME";
    private static final String SURNAME = "SURNAME";
    private static final String PASSWORD = "PASSWORD";

    @Inject
    PersonRepository repository;

    @Test
    public void createSupervisor() throws Exception {

        SignUpData payload = new SignUpData()
                .setDni(DNI)
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setName(NAME)
                .setSurnames(SURNAME)
                .setPassword(PASSWORD);

        given()
                .body(payload)
        .when()
                .post(host + SignUpRouter.V1_SIGN_UP_SUPERVISOR)
        .then()
                .statusCode(HttpResponse.StatusCode.CREATED)
                .body("success", equalTo(true))
                .body("data.dni", equalTo(DNI))
                .body("data.username", equalTo(USERNAME))
                .body("data.email", equalTo(EMAIL))
                .body("data.name", equalTo(NAME))
                .body("data.surnames", equalTo(SURNAME))
                .body("data.password", equalTo(null))
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toString().toLowerCase()))
                .body("error", equalTo(null))
        ;

        Optional<Person> byEmail = repository.findByEmail(EMAIL);
        repository.delete(byEmail.get().getId());
    }

}
