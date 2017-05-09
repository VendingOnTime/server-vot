package unit.com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

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
    private final static String COMPANY_ID = "COMPANY_ID";
    private final static String PERSON_ID = "PERSON_ID";

    private AddMachineData machineData;
    private Person person;
    private Company company;

    @Before
    public void setUp() throws Exception {
        company = FixtureFactory.generateCompanyWithOwner().setId(COMPANY_ID);
        person = company.getOwner().setId(PERSON_ID);
        machineData = FixtureFactory.generateAddMachineData().setRequester(person);
    }

    @After
    public void tearDown() throws Exception {
        company = null;
        person = null;
        machineData = null;
    }

    @Test
    public void validate_validData() {
        assertArrayEquals(new String[]{}, machineData.validate());
    }

    @Test
    public void validate_invalid_nullMachineLocation() {
        machineData.setLocation(null);

        assertArrayEquals(new String[]{INVALID_LOCATION}, machineData.validate());
    }

    @Test
    public void validate_invalid_nullMachineLocationName() {
        MachineLocation location = new MachineLocation().setName(null);
        machineData.setLocation(location);

        assertArrayEquals(new String[]{INVALID_LOCATION_NAME}, machineData.validate());
    }

    @Test
    public void validate_machineLocationName_betweenBoundaries() {
        MachineLocation locationWithMinLength = new MachineLocation().setName(StringUtils.createFilled(MIN_LOCATION_NAME_LENGTH));
        MachineLocation locationWithMaxLength = new MachineLocation().setName(StringUtils.createFilled(MAX_LOCATION_NAME_LENGTH));
        MachineLocation locationWithLengthBetweenBoundaries = new MachineLocation().setName(StringUtils.createFilled(MAX_LOCATION_NAME_LENGTH - 1));

        machineData.setLocation(locationWithMinLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setLocation(locationWithMaxLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setLocation(locationWithLengthBetweenBoundaries);
        assertArrayEquals(new String[]{}, machineData.validate());
    }

    @Test
    public void validate_invalid_shortMachineLocationName() {
        MachineLocation location = new MachineLocation().setName("a");
        machineData.setLocation(location);

        assertArrayEquals(new String[]{SHORT_LOCATION_NAME}, machineData.validate());
    }

    @Test
    public void validate_invalid_longMachineLocationName() {
        MachineLocation location = new MachineLocation().setName(StringUtils.createFilled(MAX_LOCATION_NAME_LENGTH + 1));
        machineData.setLocation(location);

        assertArrayEquals(new String[]{LONG_LOCATION_NAME}, machineData.validate());
    }

    @Test
    public void validate_invalid_nullMachineDescription() {
        machineData.setDescription(null);

        assertArrayEquals(new String[]{INVALID_DESCRIPTION}, machineData.validate());
    }

    @Test
    public void validate_machineDescription_betweenBoundaries() {
        String descriptionWithMinLength = StringUtils.createFilled(MIN_DESCRIPTION_LENGTH);
        String descriptionWithMaxLength = StringUtils.createFilled(MAX_DESCRIPTION_LENGTH);
        String descriptionWithLengthBetweenBoundaries = StringUtils.createFilled(MAX_DESCRIPTION_LENGTH - 1);

        machineData.setDescription(descriptionWithMinLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setDescription(descriptionWithMaxLength);
        assertArrayEquals(new String[]{}, machineData.validate());

        machineData.setDescription(descriptionWithLengthBetweenBoundaries);
        assertArrayEquals(new String[]{}, machineData.validate());
    }

    @Test
    public void validate_invalid_longMachineDescription() {
        String description = StringUtils.createFilled(MAX_DESCRIPTION_LENGTH + 1);
        machineData.setDescription(description);

        assertArrayEquals(new String[]{LONG_DESCRIPTION}, machineData.validate());
    }

    @Test
    public void validate_withNullMachineType_shouldReturn_emptyMachineType() {
        machineData.setType(null);

        assertArrayEquals(new String[]{EMPTY_TYPE}, machineData.validate());
    }

    @Test
    public void validate_withNullMachineState_shouldReturn_emptyMachineState() {
        machineData.setState(null);

        assertArrayEquals(new String[]{EMPTY_STATE}, machineData.validate());
    }


    @Test
    public void validate_invalid_variousErrors() {
        MachineLocation invalidMachineLocation = new MachineLocation().setName(StringUtils.createFilled(MAX_LOCATION_NAME_LENGTH + 1));
        String invalidDescription = StringUtils.createFilled(MAX_DESCRIPTION_LENGTH + 1);

        machineData
                .setLocation(invalidMachineLocation)
                .setDescription(invalidDescription)
                .setState(null)
                .setType(null);

        assertArrayEquals(new String[]{LONG_LOCATION_NAME, LONG_DESCRIPTION, EMPTY_TYPE, EMPTY_STATE}, machineData.validate());
    }

    @Test
    public void validRequester() {
        assertTrue(machineData.requesterIsAuthorized());
    }

    @Test
    public void invalidRequester_WithNullRequester() {
        machineData.setRequester(null);

        assertFalse(machineData.requesterIsAuthorized());
    }

    @Test
    public void invalidRequester_WithIncorrectRole() {
        machineData.getRequester().setRole(PersonRole.CUSTOMER);

        assertFalse(machineData.requesterIsAuthorized());
    }

    @Test
    public void invalidRequester_WithNullCompany() {
        machineData.getRequester().setCompany(null);

        assertFalse(machineData.requesterIsAuthorized());
    }

}