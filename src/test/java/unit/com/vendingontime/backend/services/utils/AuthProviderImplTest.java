package unit.com.vendingontime.backend.services.utils;

import com.vendingontime.backend.models.AbstractEntity;
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
    public void canModify_nullRequester_isFalse() throws Exception {
        assertThat(authProvider.canModify(null, requester), is(false));
    }

    @Test
    public void canModify_nullPerson_isFalse() throws Exception {
        Person person = null;
        assertThat(authProvider.canModify(requester, person), is(false));
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
    public void canModify_company_withNullCompany_isFalse() throws Exception {
        Company company = null;
        assertThat(authProvider.canModify(requester, company), is(false));
    }

    @Test
    public void canModify_company_nullPerson_isFalse() throws Exception {
        Person supervisor = FixtureFactory.generateSupervisorWithCompany();
        assertThat(authProvider.canModify(null, supervisor.getOwnedCompany()), is(false));
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
    public void canModify_machine_withNoMachine_isFalse() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = null;
        company.addWorker(FixtureFactory.generateTechnician());

        assertThat(authProvider.canModify(company.getOwner(), machine), is(false));
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
    public void canModify_machine_nullPerson_isFalse() throws Exception {
        Machine machine = FixtureFactory.generateMachine();
        assertThat(authProvider.canModify(null, machine), is(false));
    }

    @Test
    public void canModify_abstractEntity_person_isTrue() throws Exception {
        assertThat(authProvider.canModify(requester, (AbstractEntity) requester), is(true));
    }

    @Test
    public void canModify_abstractEntity_company_isTrue() throws Exception {
        Person supervisor = FixtureFactory.generateSupervisorWithCompany();
        assertThat(authProvider.canModify(supervisor, (AbstractEntity) supervisor.getOwnedCompany()), is(true));
    }

    @Test
    public void canModify_abstractEntity_machine_isTrue() throws Exception {
        Company company = FixtureFactory.generateCompanyWithOwner();
        Machine machine = FixtureFactory.generateMachine();
        company.addMachine(machine);
        company.addWorker(FixtureFactory.generateTechnician());

        assertThat(authProvider.canModify(company.getOwner(), (AbstractEntity) machine), is(true));
    }

    @Test
    public void canModify_abstractEntity_nullEntity_isFalse() throws Exception {
        AbstractEntity entity = null;
        assertThat(authProvider.canModify(requester, entity), is(false));
    }

    @Test
    public void canModify_abstractEntity_unMatched_isFalse() throws Exception {
        assertThat(authProvider.canModify(requester, new AbstractEntity() {
            @Override
            public void updateWith(AbstractEntity entity) {

            }
        }), is(false));
    }
}