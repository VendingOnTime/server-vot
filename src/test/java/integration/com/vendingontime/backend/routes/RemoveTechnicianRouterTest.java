package integration.com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.routes.RemoveMachineRouter;
import com.vendingontime.backend.routes.RemoveTechnicianRouter;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.services.AddMachineService;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.FixtureFactory;

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
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class RemoveTechnicianRouterTest extends IntegrationTest {
    @Inject private SignUpService signUpService;
    @Inject private RemoveTechnicianRouter router;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void removeMachine() throws Exception {
        Person requester = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician = signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(requester));

        AppRoute post = router.remove(technician.getId(), requester);
        String result = (String) post.handle(mock(Request.class), mock(Response.class));

        ObjectMapper mapper = new ObjectMapper();
        RESTResult restResult = mapper.readValue(result, RESTResult.class);
        assertTrue(restResult.getSuccess());

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}