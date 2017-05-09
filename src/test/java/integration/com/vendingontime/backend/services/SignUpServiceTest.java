package integration.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.SignUpService;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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

public class SignUpServiceTest extends IntegrationTest {

    @Inject private SignUpService service;
    @Inject private PersonRepository personRepository;
    @Inject private CompanyRepository companyRepository;

    private SignUpData supervisorData;
    private AddTechnicianData technicianData;

    @Before
    public void setUp() throws Exception {
        supervisorData = FixtureFactory.generateSignUpData();
        technicianData = FixtureFactory.generateAddTechnicianData();
    }

    @After
    public void tearDown() throws Exception {
        supervisorData = null;
        technicianData = null;
    }

    @Test
    public void createSupervisor() throws Exception {
        Person supervisor = service.createSupervisor(supervisorData);

        assertNotNull(supervisor);

        Optional<Person> byEmail = personRepository.findByEmail(supervisorData.getEmail());
        Optional<Company> company = companyRepository.findById(supervisor.getOwnedCompany().getId());

        assertThat(byEmail.isPresent(), is(true));
        assertThat(company.isPresent(), is(true));
        assertThat(company.get().getId(), notNullValue());

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void createTechnician() throws Exception {
        Person supervisor = service.createSupervisor(supervisorData);

        technicianData.setRequester(supervisor);
        Person technician = service.createTechnician(technicianData);

        Optional<Person> byEmail = personRepository.findByEmail(technicianData.getEmail());
        Optional<Company> company = companyRepository.findById(technician.getCompany().getId());


        assertThat(byEmail.isPresent(), is(true));
        assertThat(company.isPresent(), is(true));
        assertThat(company.get().getTechnicians().contains(technician), is(true));

        personRepository.deleteAll();
        companyRepository.deleteAll();
    }
}