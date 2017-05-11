package unit.com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.person.Person;

import com.vendingontime.backend.models.person.PersonCollisionException;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.JPAPersonRepository;
import com.vendingontime.backend.services.SignUpService;

import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import static com.vendingontime.backend.models.bodymodels.person.AddTechnicianData.EMPTY_REQUESTER;
import static com.vendingontime.backend.models.bodymodels.person.SignUpData.EMPTY_EMAIL;
import static com.vendingontime.backend.models.person.PersonCollisionException.*;
import static com.vendingontime.backend.services.AbstractService.INSUFFICIENT_PERMISSIONS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import java.util.Optional;

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
public class SignUpServiceTest {

    private static final String SUPERVISOR_ID = "SUPERVISOR_ID";
    private static final String COMPANY_ID = "COMPANY_ID";
    private static final String TECHNICIAN_ID = "TECHNICIAN_ID";

    private JPAPersonRepository repository;
    private CompanyRepository companyRepository;
    private SignUpService service;
    private SignUpData signUpData;
    private AuthProvider authProvider;

    private AddTechnicianData addTechnicianData;
    private Person supervisor;
    private Person technician;
    private Company company;

    @Before
    public void setUp() throws Exception {

        repository = mock(JPAPersonRepository.class);
        companyRepository = mock(CompanyRepository.class);
        authProvider = mock(AuthProvider.class);

        service = new SignUpService(repository, companyRepository, authProvider);

        signUpData = FixtureFactory.generateSignUpData().setRole(PersonRole.SUPERVISOR);
        supervisor = new Person(signUpData);

        company = new Company();

        addTechnicianData = FixtureFactory.generateAddTechnicianData();
        technician = new Person(addTechnicianData);

        when(repository.findById(SUPERVISOR_ID)).thenReturn(Optional.of(supervisor));
        when(repository.findById(TECHNICIAN_ID)).thenReturn(Optional.of(technician));

        when(companyRepository.create(any())).thenReturn(company.setId(COMPANY_ID));
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company.setId(COMPANY_ID)));
        when(companyRepository.update(any())).thenReturn(Optional.of(company));

        when(authProvider.canModify(any(Person.class), any(Company.class))).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        companyRepository = null;
        authProvider = null;
        service = null;
        signUpData = null;
        addTechnicianData = null;
        supervisor = null;
        technician = null;
        company = null;
    }

    @Test
    public void createSupervisor() {
        when(repository.create(any())).thenReturn(supervisor.setId(SUPERVISOR_ID));

        service.createSupervisor(signUpData);

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(1)).create(any());
        verify(companyRepository, times(1)).update(any());

        assertThat(supervisor.getOwnedCompany().getId(), notNullValue());

        Optional<Company> byId = companyRepository.findById(supervisor.getOwnedCompany().getId());
        assertThat(byId.isPresent(), is(true));
        assertThat(byId.get().getWorkers().contains(supervisor), is(true));
    }

    @Test
    public void createSupervisor_withNoRole_returnsSupervisor() throws Exception {
        when(repository.create(any())).thenReturn(supervisor.setId(SUPERVISOR_ID));

        signUpData.setRole(null);
        Person supervisor = service.createSupervisor(signUpData);

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(1)).create(any());
        verify(companyRepository, times(1)).update(any());

        assertThat(supervisor.getRole(), equalTo(PersonRole.SUPERVISOR));
        assertThat(supervisor.getOwnedCompany().getId(), notNullValue());

        Optional<Company> byId = companyRepository.findById(supervisor.getOwnedCompany().getId());
        assertThat(byId.isPresent(), is(true));
        assertThat(byId.get().getWorkers().contains(supervisor), is(true));
    }

    @Test
    public void createSupervisor_withInvalidPayload_throwsException() throws Exception {
        try {
            signUpData.setEmail("");
            service.createSupervisor(signUpData);
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{EMPTY_EMAIL}));
        }
    }

    @Test
    public void createSupervisor_withCollision_throwsException() throws Exception {
        try {
            doThrow(new PersonCollisionException(new Cause[]{Cause.EMAIL}))
                    .when(repository).create(any());
            service.createSupervisor(signUpData);
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{EMAIL_EXISTS}));
        }
    }

    @Test
    public void createTechnician() {
        when(repository.create(any())).thenReturn(technician.setId(TECHNICIAN_ID));

        company.setOwner(supervisor).addWorker(supervisor);
        addTechnicianData.setRequester(supervisor);

        service.createTechnician(addTechnicianData);

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(0)).create(any());
        verify(companyRepository, times(1)).update(any());

        assertThat(technician.getCompany().getId(), notNullValue());
        assertThat(company.getWorkers().contains(technician), is(true));
    }

    @Test
    public void createTechnician_withNoRole_returnsTechnician() {
        when(repository.create(any())).thenReturn(technician.setId(TECHNICIAN_ID));

        company.setOwner(supervisor).addWorker(supervisor);
        addTechnicianData.setRequester(supervisor).setRole(null);

        service.createTechnician(addTechnicianData);

        verify(repository, times(1)).create(any());
        verify(companyRepository, times(0)).create(any());
        verify(companyRepository, times(1)).update(any());

        assertThat(technician.getRole(), is(PersonRole.TECHNICIAN));
    }

    @Test
    public void createTechnician_withNoPermissions_throwsException() throws Exception {
        when(authProvider.canModify(any(Person.class), any(Company.class))).thenReturn(false);

        try {
            addTechnicianData.setRequester(supervisor);
            service.createTechnician(addTechnicianData);
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{INSUFFICIENT_PERMISSIONS}));
        }
    }

    @Test
    public void createTechnician_withInvalidPayload_throwsException() throws Exception {
        try {
            company.setOwner(supervisor).addWorker(supervisor);
            addTechnicianData.setRequester(supervisor);
            addTechnicianData.setEmail("");
            service.createTechnician(addTechnicianData);
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{EMPTY_EMAIL}));
        }
    }

    @Test
    public void createTechnician_withCollision_throwsException() throws Exception {
        try {
            doThrow(new PersonCollisionException(new Cause[]{Cause.EMAIL}))
                    .when(repository).create(any());

            company.setOwner(supervisor).addWorker(supervisor);
            addTechnicianData.setRequester(supervisor);

            addTechnicianData.setRequester(supervisor);
            service.createTechnician(addTechnicianData);
            fail();
        } catch (BusinessLogicException e) {
            assertThat(e.getCauses(), equalTo(new String[]{EMAIL_EXISTS}));
        }
    }
}