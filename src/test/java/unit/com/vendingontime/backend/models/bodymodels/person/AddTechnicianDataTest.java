package unit.com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.bodymodels.person.AddTechnicianData;
import com.vendingontime.backend.models.person.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.person.AddTechnicianData.*;
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

public class AddTechnicianDataTest {

    private AddTechnicianData addTechnicianData;

    @Before
    public void setUp() throws Exception {
        addTechnicianData = FixtureFactory.generateAddTechnicianData()
                .setRequester(FixtureFactory.generateSupervisorWithCompany());
    }

    @After
    public void tearDown() throws Exception {
        addTechnicianData = null;
    }

    @Test
    public void validate_validData() {
        assertArrayEquals(new String[]{}, addTechnicianData.validate());
    }

    @Test
    public void validate_withEmptyRequester_notValid() throws Exception {
        addTechnicianData.setRequester(null);

        assertArrayEquals(new String[]{EMPTY_REQUESTER}, addTechnicianData.validate());
    }

    @Test
    public void validate_invalid_multipleErrors() throws Exception {
        AddTechnicianData technicianData = FixtureFactory.generateAddTechnicianData();
        technicianData.setRequester(null);
        technicianData.setEmail("");

        assertArrayEquals(new String[]{
                EMPTY_EMAIL, EMPTY_REQUESTER
        }, technicianData.validate());
    }
}