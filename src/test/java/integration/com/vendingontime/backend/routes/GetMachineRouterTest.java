package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.GetMachineRouter;
import com.vendingontime.backend.routes.utils.RESTResult;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
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

public class GetMachineRouterTest extends IntegrationTest {

    @Inject private GetMachineRouter router;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;
    @Inject private MachineRepository machineRepository;

    @Test
    public void getMachine() throws Exception {
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Machine machine = machineRepository.create(FixtureFactory.generateMachine());

        Person savedOwner = personRepository.findById(company.getOwner().getId()).get();
        Machine savedMachine = machineRepository.findById(machine.getId()).get();

        company.addMachine(savedMachine);
        companyRepository.update(company);

        String result = (String) router.getWith(savedMachine.getId(), savedOwner)
                .handle(mock(Request.class), mock(Response.class));

        ObjectMapper mapper = new ObjectMapper();
        RESTResult restResult = mapper.readValue(result, RESTResult.class);
        assertThat(restResult.getSuccess(), is(true));
        assertNotNull(restResult.getData());

        machineRepository.deleteAll();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

}