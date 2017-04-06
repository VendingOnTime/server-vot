package integration.com.vendingontime.backend.repositories;

import com.google.inject.Inject;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;
import com.vendingontime.backend.repositories.JPAMachineRepository;
import integration.com.vendingontime.backend.repositories.testutils.IntegrationTest;
import org.junit.*;

import java.util.Optional;

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
    private static final String NAME = "NAME";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final MachineState STATE = MachineState.OPERATIVE;
    private static final MachineType TYPE = MachineType.COFFEE;


    @Inject
    private JPAMachineRepository repository;

    private Machine machineOne;
    private Machine machineTwo;

    @Before
    public void setUp() throws Exception {
        MachineLocation location = new MachineLocation();
        location.setName(NAME);

        machineOne = new Machine();
        machineOne.setLocation(location);
        machineOne.setDescription(DESCRIPTION);
        machineOne.setState(STATE);
        machineOne.setType(TYPE);

        machineTwo = new Machine();
        machineTwo.setLocation(location);
        machineTwo.setDescription(DESCRIPTION);
        machineTwo.setState(STATE);
        machineTwo.setType(TYPE);
    }

    @After
    public void tearDown() throws Exception {
        repository = null;
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

        repository.delete(machineId);

        Optional<Machine> possiblePerson = repository.findById(machineId);
        assertFalse(possiblePerson.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void delete_null_returnsEmpty() throws Exception {
        repository.delete(null);
    }
}