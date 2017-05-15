package integration.com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.GetPersonService;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.Test;
import testutils.FixtureFactory;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

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
public class GetPersonServiceTest extends IntegrationTest {
    @Inject private SignUpService signUpService;
    @Inject private GetPersonService service;

    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    @Test
    public void getMachineData_forValidPersonId_andAuthorizedUser() {
        Person supervisor = signUpService.createSupervisor(FixtureFactory.generateSignUpData());
        Person technician =
                signUpService.createTechnician(FixtureFactory.generateAddTechnicianData().setRequester(supervisor));

        PersonRequest personRequest = FixtureFactory.generatePersonRequestFrom(technician, supervisor);
        Person foundTechnician = service.getBy(personRequest).get();
        assertThat(foundTechnician, equalTo(technician));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}