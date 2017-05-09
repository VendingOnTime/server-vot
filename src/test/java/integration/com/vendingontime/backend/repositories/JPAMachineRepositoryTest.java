package integration.com.vendingontime.backend.repositories;

import com.google.inject.Inject;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.JPACompanyRepository;
import com.vendingontime.backend.repositories.JPAMachineRepository;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.*;
import testutils.FixtureFactory;

import java.util.List;
import java.util.Optional;

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
public class JPAMachineRepositoryTest extends IntegrationTest {

    @Inject private JPAMachineRepository repository;
    @Inject private JPACompanyRepository companyRepository;

    private Machine machineOne;
    private Machine machineTwo;

    @Before
    public void setUp() throws Exception {
        machineOne = FixtureFactory.generateMachine();
        machineTwo = FixtureFactory.generateMachine();
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
        companyRepository = null;
        machineOne = null;
        machineTwo = null;
    }

    @Test
    public void create() throws Exception {
        Machine machine = repository.create(this.machineOne);
        String machineId = machine.getId();

        Optional<Machine> possibleMachine = repository.findById(machine.getId());
        assertEquals(machine, possibleMachine.get());

        repository.delete(machineId);
    }

    @Test
    public void create_twoWithoutCollisions() throws Exception {
        Machine machineOne = repository.create(this.machineOne);
        String machineOneId = machineOne.getId();
        Machine machineTwo = repository.create(this.machineTwo);
        String machineTwoId = machineTwo.getId();

        Optional<Machine> possibleMachineOne = repository.findById(machineOneId);
        assertEquals(machineOne, possibleMachineOne.get());
        Optional<Machine> possibleMachineTwo = repository.findById(machineTwoId);
        assertEquals(machineTwo, possibleMachineTwo.get());

        repository.delete(machineOneId);
        repository.delete(machineTwoId);
    }

    @Test(expected = NullPointerException.class)
    public void create_null() throws Exception {
        repository.create(null);
    }

    @Test
    public void create_withIDExistent() throws Exception {
        Machine machine = repository.create(this.machineOne);

        assertEquals(machine, repository.create(machine));

        repository.delete(machine.getId());
    }


    @Test
    public void findById() throws Exception {
        Machine machine = repository.create(this.machineOne);
        String machineId = machine.getId();

        Optional<Machine> possibleMachine = repository.findById(machineId);
        assertTrue(possibleMachine.isPresent());
        assertEquals(machineId, possibleMachine.get().getId());

        repository.delete(machineId);
    }

    @Test
    public void findById_null_returnsEmpty() throws Exception {
        Optional<Machine> possibleMachine = repository.findById(null);
        assertFalse(possibleMachine.isPresent());
    }

    @Test
    public void update() throws Exception {
        final String NEW_DESCRIPTION = "NEW DESCRIPTION";
        Machine machine = repository.create(this.machineOne);
        String machineId = machine.getId();

        machine.setDescription(NEW_DESCRIPTION);
        repository.update(machine);

        Optional<Machine> possibleMachine = repository.findById(machineId);
        assertEquals(NEW_DESCRIPTION, possibleMachine.get().getDescription());

        repository.delete(machineId);
    }

    @Test(expected = NullPointerException.class)
    public void update_null_returnsEmpty() throws Exception {
        repository.update(null);
    }

    @Test
    public void delete() throws Exception {
        Machine machine = repository.create(this.machineOne);
        String machineId = machine.getId();

        Optional<Machine> possibleDeleted = repository.delete(machineId);
        assertThat(possibleDeleted.isPresent(), is(true));

        Optional<Machine> possiblePerson = repository.findById(machineId);
        assertFalse(possiblePerson.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void delete_null_returnsEmpty() throws Exception {
        repository.delete(null);
    }

    @Test
    public void findByCompany_companyNotExists_returnsEmpty() throws Exception {
        Company company = new Company().setId("FAKE_ID");
        List<Machine> machines = repository.findMachinesByCompany(company);
        assertThat(machines.size(), is(0));
    }

    @Test
    public void findByCompany_nullCompany_returnsEmpty() throws Exception {
        List<Machine> machines = repository.findMachinesByCompany(null);
        assertThat(machines.size(), is(0));
    }

    @Test
    public void findByCompany_noSavedCompany_returnsEmpty() throws Exception {
        List<Machine> machines = repository.findMachinesByCompany(new Company());
        assertThat(machines.size(), is(0));
    }

    @Test
    public void findByCompany_companyExistsWithNoMachines_returnsEmpty() throws Exception {
        Company company = companyRepository.create(new Company());
        List<Machine> machines = repository.findMachinesByCompany(company);
        assertThat(machines.size(), is(0));
        companyRepository.delete(company.getId());
    }

    @Test
    public void findByCompany_companyExistsWithMachines_returnsMachines() throws Exception {
        Company company = companyRepository.create(new Company());

        Machine machine1 = repository.create(new Machine());
        Machine machine2 = repository.create(new Machine());

        Machine savedMachine1 = repository.findById(machine1.getId()).get();
        Machine savedMachine2 = repository.findById(machine2.getId()).get();

        company.addMachine(savedMachine1);
        company.addMachine(savedMachine2);

        companyRepository.update(company);

        List<Machine> machines = repository.findMachinesByCompany(company);
        assertThat(machines.size(), is(2));
        assertThat(machines.contains(savedMachine1), is(true));
        assertThat(machines.contains(savedMachine2), is(true));

        repository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public void deleteAll() throws Exception {
        Machine machine = repository.create(this.machineOne);
        String machineId = machine.getId();

        repository.deleteAll();

        Optional<Machine> possibleMachine = repository.findById(machineId);
        assertFalse(possibleMachine.isPresent());
    }
}