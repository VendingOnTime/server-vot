package unit.com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.person.EditPersonData.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
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
public class EditPersonDataTest {
    private final static String PERSON_ID = "PERSON_ID";

    private EditPersonData editPersonData;

    @Before
    public void setUp() throws Exception {
        editPersonData = FixtureFactory.generateEditPersonData()
                .setId(PERSON_ID)
                .setRequester(FixtureFactory.generateSupervisor());
    }

    @After
    public void tearDown() throws Exception {
        editPersonData = null;
    }

    @Test
    public void validate_validData() {
        assertArrayEquals(new String[]{}, editPersonData.validate());
    }

    @Test
    public void validate_withNullPersonId_notValid() throws Exception {
        editPersonData.setId(null);

        assertThat(editPersonData.validate(), equalTo(new String[]{EMPTY_PERSON_ID}));
    }

    @Test
    public void validate_withEmptyPersonId_notValid() throws Exception {
        editPersonData.setId("  ");

        assertThat(editPersonData.validate(), equalTo(new String[]{EMPTY_PERSON_ID}));
    }

    @Test
    public void validate_withEmptyRequester_notValid() throws Exception {
        editPersonData.setRequester(null);

        assertThat(editPersonData.validate(), equalTo(new String[]{EMPTY_REQUESTER}));
    }

    @Test
    public void validate_withRequesterWithoutRole_notValid() throws Exception {
        editPersonData.setRequester(new Person());

        assertThat(editPersonData.validate(), equalTo(new String[]{EMPTY_REQUESTER}));
    }

    @Test
    public void validate_invalid_multipleErrors() throws Exception {
        editPersonData
                .setId(null)
                .setRequester(null)
                .setEmail("")
                .setDni("")
                .setUsername("")
                .setRole(PersonRole.SUPERVISOR);

        assertThat(editPersonData.validate(), equalTo(new String[]{EMPTY_EMAIL, EMPTY_USERNAME, EMPTY_PERSON_ID, EMPTY_REQUESTER}));
    }
}