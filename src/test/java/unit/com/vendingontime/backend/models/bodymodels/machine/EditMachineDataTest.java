package unit.com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.machine.AddMachineData.*;
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

public class EditMachineDataTest {
    private final static String COMPANY_ID = "COMPANY_ID";
    private final static String PERSON_ID = "PERSON_ID";
    private final static String MACHINE_ID = "MACHINE_ID";

    private EditMachineData machineData;
    private Person person;
    private Company company;

    @Before
    public void setUp() throws Exception {
        company = FixtureFactory.generateCompanyWithOwner().setId(COMPANY_ID);
        person = company.getOwner().setId(PERSON_ID);
        machineData = (EditMachineData)
                FixtureFactory.generateEditMachineData().setId(MACHINE_ID).setRequester(person);
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
    public void validate_withNullMachineId_notValid() throws Exception {
        machineData.setId(null);

        assertArrayEquals(new String[]{EditMachineData.EMPTY_MACHINE_ID}, machineData.validate());
    }

    @Test
    public void validate_withEmptyMachineId_notValid() throws Exception {
        machineData.setId("  ");

        assertArrayEquals(new String[]{EditMachineData.EMPTY_MACHINE_ID}, machineData.validate());
    }
}