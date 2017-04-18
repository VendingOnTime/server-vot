package integration.com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.CompanyRepository;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import javax.inject.Inject;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
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

public class JPACompanyRepositoryTest extends IntegrationTest {

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private MachineRepository machineRepository;

    private Company company;
    private Person owner;
    private Machine machine;

    @Before
    public void setUp() throws Exception {
        company = FixtureFactory.generateCompany();
        owner = FixtureFactory.generateSupervisor();
        machine = FixtureFactory.generateMachine();
    }

    @After
    public void tearDown() throws Exception {
        company = null;
        owner = null;
        machine = new Machine();
    }

    @Test
    public void create() throws Exception {
        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        assertThat(companyId, notNullValue());

        companyRepository.delete(companyId);
    }

    @Test
    public void create_withAlreadyCreatedEntity_returnsSameEntity() throws Exception {
        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        Company sameCompany = companyRepository.create(company);
        String sameCompanyId = sameCompany.getId();

        assertThat(sameCompanyId, equalTo(companyId));

        companyRepository.delete(companyId);
    }

    @Test
    public void findById() throws Exception {
        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        Optional<Company> byId = companyRepository.findById(companyId);
        assertThat(byId.isPresent(), is(true));
        assertThat(byId.get().getId(), equalTo(companyId));

        companyRepository.delete(companyId);
    }

    @Test
    public void findById_nullReturnsEmpty() throws Exception {
        Optional<Company> byId = companyRepository.findById(null);

        assertThat(byId.isPresent(), is(false));
    }

    @Test
    public void update_withOwner() throws Exception {
        Person owner = personRepository.create(this.owner);
        String ownerId = owner.getId();

        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        Person savedOwner = personRepository.findById(ownerId).get();
        company.setOwner(savedOwner);
        Optional<Company> possibleUpdated = companyRepository.update(company);

        assertThat(possibleUpdated.isPresent(), is(true));
        assertThat(possibleUpdated.get().getOwner(), equalTo(owner));
        assertThat(savedOwner.getCompany(), equalTo(company));

        companyRepository.delete(companyId);
        personRepository.delete(ownerId);
    }

    @Test
    public void update_withMachine() throws Exception {
        Machine machine = machineRepository.create(this.machine);
        String machineId = machine.getId();

        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        Machine savedMachine = machineRepository.findById(machineId).get();

        company.addMachine(savedMachine);
        Optional<Company> possibleUpdated = companyRepository.update(company);

        assertThat(possibleUpdated.isPresent(), is(true));
        assertThat(possibleUpdated.get().getMachines().contains(machine), is(true));
        assertThat(savedMachine.getCompany(), equalTo(company));

        machineRepository.delete(machineId);
        companyRepository.delete(companyId);
    }

    @Test(expected = NullPointerException.class)
    public void update_withNull_returnsException() throws Exception {
        companyRepository.update(null);
    }

    @Test
    public void delete() throws Exception {
        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        Optional<Company> possibleDeleted = companyRepository.delete(companyId);
        assertThat(possibleDeleted.isPresent(), is(true));

        Optional<Company> byId = companyRepository.findById(companyId);
        assertThat(byId.isPresent(), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void delete_withNull_returnsException() throws Exception {
        companyRepository.delete(null);
    }

    @Test
    public void deleteAll() throws Exception {
        Company company = companyRepository.create(this.company);
        String companyId = company.getId();

        companyRepository.deleteAll();

        Optional<Company> byId = companyRepository.findById(companyId);
        assertThat(byId.isPresent(), is(false));
    }
}