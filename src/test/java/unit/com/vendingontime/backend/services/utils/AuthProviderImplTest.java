package unit.com.vendingontime.backend.services.utils;

import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.services.utils.AuthProviderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

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

public class AuthProviderImplTest {

    private AuthProviderImpl authProvider;

    private Person requester;

    @Before
    public void setUp() throws Exception {
        authProvider = new AuthProviderImpl();
        requester = FixtureFactory.generateSupervisor();
    }

    @After
    public void tearDown() throws Exception {
        authProvider = null;
        requester = null;
    }

    @Test
    public void canModify_himself_isTrue() throws Exception {
        assertThat(authProvider.canModify(requester, requester), is(true));
    }

    @Test
    public void canModify_asAnOwnerACompanyTechnician_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Person technician = FixtureFactory.generateTechnician();
        company.addWorker(technician);

        assertThat(authProvider.canModify(company.getOwner(), technician), is(true));
    }

    @Test
    public void canModify_asAnOwnerANonCompanyTechnician_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Person technician = FixtureFactory.generateTechnician();

        assertThat(authProvider.canModify(company.getOwner(), technician), is(false));
    }

    @Test
    public void canModify_nonRelatedOne_isFalse() throws Exception {
        assertThat(authProvider.canModify(requester, FixtureFactory.generateCustomer()), is(false));
    }

    @Test
    public void canModifyPassword_himself_isTrue() throws Exception {
        assertThat(authProvider.canModifyPassword(requester, requester), is(true));
    }

    @Test
    public void canModifyPassword_nonRelatedOne_isFalse() throws Exception {
        assertThat(authProvider.canModifyPassword(requester, FixtureFactory.generateCustomer()), is(false));
    }

    @Test
    public void canModifyPassword_nullPerson_isFalse() throws Exception {
        assertThat(authProvider.canModifyPassword(null, requester), is(false));
    }

    @Test
    public void canModify_company_withPermissions_isTrue() throws Exception {
        Person supervisor = FixtureFactory.generateSupervisorWithCompany();
        assertThat(authProvider.canModify(supervisor, supervisor.getOwnedCompany()), is(true));
    }

    @Test
    public void canModify_company_withNoPermissions_isFalse() throws Exception {
        assertThat(authProvider.canModify(requester, FixtureFactory.generateCompany()), is(false));
    }

    @Test
    public void canModify_machine_withPermissions_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = FixtureFactory.generateMachine();
        company.addMachine(machine);
        company.addWorker(FixtureFactory.generateTechnician());

        assertThat(authProvider.canModify(company.getOwner(), machine), is(true));
    }

    @Test
    public void canModify_machine_withNoCompany_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = FixtureFactory.generateMachine();
        company.addWorker(FixtureFactory.generateTechnician());

        assertThat(authProvider.canModify(company.getOwner(), machine), is(false));
    }

    @Test
    public void canModify_machine_withTechnician_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = FixtureFactory.generateMachine();
        Person technician = FixtureFactory.generateTechnician();
        company.addWorker(technician);

        assertThat(authProvider.canModify(technician, machine), is(false));
    }

    @Test
    public void canModify_abstractEntity_nullEntity_isFalse() throws Exception {
        AbstractEntity entity = null;
        assertThat(authProvider.canModify(requester, entity), is(false));
    }

    @Test
    public void canModify_abstractEntity_nullRequester_isFalse() throws Exception {
        Person requester = null;
        Person supervisor = FixtureFactory.generateSupervisorWithCompany();
        assertThat(authProvider.canModify(requester, supervisor), is(false));
    }

    @Test
    public void canModify_abstractEntity_unMatched_isFalse() throws Exception {
        assertThat(authProvider.canModify(requester, new AbstractEntity() {
            @Override
            public void updateWith(AbstractEntity entity) {

            }
        }), is(false));
    }

    @Test
    public void canSee_abstractEntity_nullEntity_isFalse() throws Exception {
        AbstractEntity entity = null;
        assertThat(authProvider.canSee(requester, entity), is(false));
    }

    @Test
    public void canSee_abstractEntity_nullRequester_isFalse() throws Exception {
        Person requester = null;
        Person supervisor = FixtureFactory.generateSupervisorWithCompany();
        assertThat(authProvider.canSee(requester, supervisor), is(false));
    }

    @Test
    public void canSee_abstractEntity_unMatched_isFalse() throws Exception {
        assertThat(authProvider.canSee(requester, new AbstractEntity() {
            @Override
            public void updateWith(AbstractEntity entity) {

            }
        }), is(false));
    }

    @Test
    public void canSee_machine_supervisor_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = FixtureFactory.generateMachine();
        company.addMachine(machine);

        assertThat(authProvider.canSee(company.getOwner(), machine), is(true));
    }

    @Test
    public void canSee_machine_technician_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = FixtureFactory.generateMachine();
        company.addMachine(machine);
        Person technician = FixtureFactory.generateTechnician();
        company.addWorker(technician);

        assertThat(authProvider.canSee(technician, machine), is(true));
    }

    @Test
    public void canSee_machine_nonRelatedSupervisor_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner().setId("COMPANY_ID");
        Machine machine = FixtureFactory.generateMachine();
        company.addMachine(machine);
        Company anotherCompany = FixtureFactory.generateCompanyWithOwner().setId("ANOTHER_COMPANY");

        assertThat(authProvider.canSee(anotherCompany.getOwner(), machine), is(false));
    }

    @Test
    public void canSee_machine_nonRelatedTechnician_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner().setId("COMPANY_ID");
        Machine machine = FixtureFactory.generateMachine();
        company.addMachine(machine);
        Company anotherCompany = FixtureFactory.generateCompanyWithOwner().setId("ANOTHER_COMPANY");
        Person technician = FixtureFactory.generateTechnician();
        anotherCompany.addWorker(technician);

        assertThat(authProvider.canSee(technician, machine), is(false));
    }

    @Test
    // TODO: alberto@15/05/2017 Maybe this one should return true in a near future
    public void canSee_machine_personWithNoCompany_isFalse() throws Exception {
        Person customer = FixtureFactory.generateCustomer();
        Machine machine = FixtureFactory.generateMachine();

        assertThat(authProvider.canSee(customer, machine), is(false));
    }

    @Test
    public void canSee_person_supervisorATechnician_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Person technician = FixtureFactory.generateTechnician();
        company.addWorker(technician);

        assertThat(authProvider.canSee(company.getOwner(), technician), is(true));
    }

    @Test
    public void canSee_person_supervisorANonCompanyTechnician_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner().setId("COMPANY_ID");
        Person technician = FixtureFactory.generateTechnician();
        company.addWorker(technician);
        Person supervisor = FixtureFactory.generateCompanyWithOwner().setId("ANOTHER_COMPANY").getOwner();

        assertThat(authProvider.canSee(supervisor, technician), is(false));
    }

    @Test
    public void canSee_person_customerATechnician_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompany().setId("COMPANY_ID");
        Person technician = FixtureFactory.generateTechnician();
        company.addWorker(technician);
        Person customer = FixtureFactory.generateCustomer();

        assertThat(authProvider.canSee(customer, technician), is(false));
    }

    @Test
    public void canSee_person_customerASupervisor_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner().setId("COMPANY_ID");
        Person customer = FixtureFactory.generateCustomer();

        assertThat(authProvider.canSee(customer, company.getOwner()), is(false));
    }

    @Test
    // TODO: alberto@15/05/2017 Maybe this one should return true in a near future
    public void canSee_person_supervisorACustomer_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner().setId("COMPANY_ID");
        Person customer = FixtureFactory.generateCustomer();

        assertThat(authProvider.canSee(company.getOwner(), customer), is(false));
    }
}