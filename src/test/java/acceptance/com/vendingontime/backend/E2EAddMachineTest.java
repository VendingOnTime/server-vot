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
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.person.LogInData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AddMachineRouter;
import com.vendingontime.backend.routes.LogInRouter;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.LogInService;
import com.vendingontime.backend.services.SignUpService;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.List;

import static io.restassured.RestAssured.given;

public class E2EAddMachineTest extends E2ETest {
    @Inject
    private AddMachineService service;

    @Inject
    private SignUpService signUpService;

    @Inject
    private LogInService logInService;

    @Inject
    private MachineRepository repository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Test
    public void addMachine() {
        SignUpData signUpData = FixtureFactory.generateSignUpData();
        Person supervisor = signUpService.createSupervisor(signUpData);
        Person savedSupervisor = personRepository.findByDni(supervisor.getDni()).get();
        String token = logInService.authorizeUser(FixtureFactory.generateLogInDataFrom(savedSupervisor));

        AddMachineData payload = FixtureFactory.generateAddMachineData();

        given()
            .header("Authorization", "JWT " + token)
            .body(payload)
        .when()
            .post(host + AddMachineRouter.V1_MACHINES)
        .then()
            .statusCode(200);

        List<Machine> machinesByCompany = repository.findMachinesByCompany(savedSupervisor.getCompany());

        for (Machine machine : machinesByCompany) {
            repository.delete(machine.getId());
        }

        companyRepository.delete(savedSupervisor.getCompany().getId());

        personRepository.delete(supervisor.getId());
    }
}
