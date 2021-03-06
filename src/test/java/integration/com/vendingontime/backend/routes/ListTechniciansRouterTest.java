package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.ListTechniciansRouter;
import com.vendingontime.backend.routes.utils.RESTResult;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
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
public class ListTechniciansRouterTest extends IntegrationTest {

    @Inject private ListTechniciansRouter router;
    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void listFor_owner() throws Exception {
        Company company = companyRepository.create(FixtureFactory.generateCompanyWithOwner());
        Person technician1 = personRepository.create(new Person().setRole(PersonRole.TECHNICIAN));
        Person technician2 = personRepository.create(new Person().setRole(PersonRole.TECHNICIAN));

        Person savedOwner = personRepository.findById(company.getOwner().getId()).get();
        Person savedTechnician1 = personRepository.findById(technician1.getId()).get();
        Person savedTechnician2 = personRepository.findById(technician2.getId()).get();

        company.addWorker(savedTechnician1);
        company.addWorker(savedTechnician2);
        companyRepository.update(company);

        String result = (String) router.listFor(savedOwner)
                .handle(mock(Request.class), mock(Response.class));

        ObjectMapper mapper = new ObjectMapper();
        RESTResult restResult = mapper.readValue(result, RESTResult.class);
        assertThat(restResult.getSuccess(), is(true));

        List machines = (List) restResult.getData();
        assertThat(machines.size(), is(2));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}