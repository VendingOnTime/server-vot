package unit.com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;
import com.vendingontime.backend.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.vendingontime.backend.models.bodymodels.machine.AddMachineData.*;

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
public class AddMachineDataTest {
    private final static String DESCRIPTION = "DESCRIPTION";
    private final static String LOCATION = "LOCATION";

    private final static MachineLocation MACHINE_LOCATION = new MachineLocation().setName(LOCATION);

    public AddMachineData machineData;

    @Before
    public void setUp() throws Exception {
        machineData = new AddMachineData();

        machineData.setDescription(DESCRIPTION);
        machineData.setMachineState(MachineState.OPERATIVE);
        machineData.setMachineType(MachineType.COFFEE);
        machineData.setMachineLocation(MACHINE_LOCATION);
    }

    @After
    public void tearDown() throws Exception {
        machineData = null;
    }

    @Test
    public void validate_validData() {
        assertArrayEquals(new String[]{}, machineData.validate());
    }

    @Test
    public void validate_invalid_nullMachineLocation() {
        machineData.setMachineLocation(null);

        assertArrayEquals(new String[]{INVALID_MACHINE_LOCATION}, machineData.validate());
    }

    @Test
    public void validate_invalid_nullMachineLocationName() {
        MachineLocation location = new MachineLocation().setName(null);
        machineData.setMachineLocation(location);

        assertArrayEquals(new String[]{INVALID_MACHINE_LOCATION_NAME}, machineData.validate());
    }

    @Test
    public void validate_machineLocationName_betweenBoundaries() {
        MachineLocation locationWithMinLength = new MachineLocation().setName(StringUtils.createFilled(MIN_MACHINE_LOCATION_NAME_LENGTH));
        MachineLocation locationWithMaxLength = new MachineLocation().setName(StringUtils.createFilled(MAX_MACHINE_LOCATION_NAME_LENGTH));
        MachineLocation locationWithLengthBetweenBoundaries = new MachineLocation().setName(StringUtils.createFilled(MAX_MACHINE_LOCATION_NAME_LENGTH - 1));

        machineData.setMachineLocation(locationWithMinLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setMachineLocation(locationWithMaxLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setMachineLocation(locationWithLengthBetweenBoundaries);
        assertArrayEquals(new String[]{}, machineData.validate());
    }

    @Test
    public void validate_invalid_shortMachineLocationName() {
        MachineLocation location = new MachineLocation().setName("a");
        machineData.setMachineLocation(location);

        assertArrayEquals(new String[]{SHORT_MACHINE_LOCATION_NAME}, machineData.validate());
    }

    @Test
    public void validate_invalid_longMachineLocationName() {
        MachineLocation location = new MachineLocation().setName(StringUtils.createFilled(MAX_MACHINE_LOCATION_NAME_LENGTH + 1));
        machineData.setMachineLocation(location);

        assertArrayEquals(new String[]{LONG_MACHINE_LOCATION_NAME}, machineData.validate());
    }

    @Test
    public void validate_invalid_nullMachineDescription() {
        machineData.setDescription(null);

        assertArrayEquals(new String[]{INVALID_MACHINE_DESCRIPTION}, machineData.validate());
    }

    @Test
    public void validate_machineDescription_betweenBoundaries() {
        String descriptionWithMinLength = StringUtils.createFilled(MIN_MACHINE_DESCRIPTION_LENGTH);
        String descriptionWithMaxLength = StringUtils.createFilled(MAX_MACHINE_DESCRIPTION_LENGTH);
        String descriptionWithLengthBetweenBoundaries = StringUtils.createFilled(MAX_MACHINE_DESCRIPTION_LENGTH - 1);

        machineData.setDescription(descriptionWithMinLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setDescription(descriptionWithMaxLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setDescription(descriptionWithLengthBetweenBoundaries);
        assertArrayEquals(new String[]{}, machineData.validate());
    }

    @Test
    public void validate_invalid_longMachineDescription() {
        String description = StringUtils.createFilled(MAX_MACHINE_DESCRIPTION_LENGTH + 1);
        machineData.setDescription(description);

        assertArrayEquals(new String[]{LONG_MACHINE_DESCRIPTION}, machineData.validate());
    }

    //TODO enum tests and complete test with all kind of errors

    @Test public void validate_invalid_variousErrors() {
        MachineLocation invalidMachineLocation = new MachineLocation().setName(StringUtils.createFilled(MAX_MACHINE_LOCATION_NAME_LENGTH + 1));
        String invalidDescription = StringUtils.createFilled(MAX_MACHINE_DESCRIPTION_LENGTH + 1);

        machineData.setMachineLocation(invalidMachineLocation).setDescription(invalidDescription);

        assertArrayEquals(new String[]{LONG_MACHINE_LOCATION_NAME, LONG_MACHINE_DESCRIPTION}, machineData.validate());
    }
}