package acceptance.com.vendingontime.backend;

import acceptance.com.vendingontime.backend.testutils.E2ETest;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.SignUpRouter;
import com.vendingontime.backend.routes.utils.HttpResponse;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static io.restassured.RestAssured.given;
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
public class E2ESignUpSupervisorTest extends E2ETest {

    @Inject private PersonRepository repository;

    @Test
    public void createSupervisor() throws Exception {
        SignUpData payload = FixtureFactory.generateSignUpData();

        given()
                .body(payload)
        .when()
                .post(host + SignUpRouter.V1_SIGN_UP_SUPERVISOR)
        .then()
                .statusCode(HttpResponse.StatusCode.CREATED)
                .body("success", is(true))
                .body("data.dni", equalTo(payload.getDni()))
                .body("data.username", equalTo(payload.getUsername()))
                .body("data.email", equalTo(payload.getEmail()))
                .body("data.name", equalTo(payload.getName()))
                .body("data.surnames", equalTo(payload.getSurnames()))
                .body("data.password", nullValue())
                .body("data.role", equalTo(PersonRole.SUPERVISOR.toString().toLowerCase()))
                .body("error", nullValue());

        Optional<Person> byEmail = repository.findByEmail(payload.getEmail());
        repository.delete(byEmail.get().getId());
    }

}
