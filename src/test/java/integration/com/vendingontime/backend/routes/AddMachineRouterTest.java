package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.AddMachineRouter;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.RESTResult;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


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

public class AddMachineRouterTest extends IntegrationTest {
    @Inject
    private AddMachineRouter router;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Test
    public void addMachine() throws Exception {
        Person owner = personRepository.create(FixtureFactory.generateSupervisor());
        Company company = companyRepository.create(FixtureFactory.generateCompany());

        Person savedOwner = personRepository.findByDni(owner.getDni()).get();

        company.setOwner(savedOwner);
        companyRepository.update(company);

        ObjectMapper mapper = new ObjectMapper();
        AddMachineData machineData = FixtureFactory.generateAddMachineData();
        String stringifiedMachine = mapper.writeValueAsString(machineData);

        AppRoute post = router.addMachine(stringifiedMachine, savedOwner);
        String result = (String) post.handle(mock(Request.class), mock(Response.class));

        RESTResult restResult = mapper.readValue(result, RESTResult.class);
        assertTrue(restResult.getSuccess());
    }

}