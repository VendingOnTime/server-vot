package unit.com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.bodymodels.person.EditPasswordData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.vendingontime.backend.models.bodymodels.person.EditPasswordData.EMPTY_NEW_PASSWORD;
import static com.vendingontime.backend.models.bodymodels.person.EditPasswordData.EMPTY_OLD_PASSWORD;
import static com.vendingontime.backend.models.bodymodels.person.EditPasswordData.SHORT_NEW_PASSWORD;
import static org.hamcrest.core.IsEqual.equalTo;
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

public class EditPasswordDataTest {

    private EditPasswordData editPasswordData;

    @Before
    public void setUp() throws Exception {
        editPasswordData = new EditPasswordData()
                .setOldPassword("123456")
                .setNewPassword("7891011");
    }

    @After
    public void tearDown() throws Exception {
        editPasswordData = null;
    }

    @Test
    public void validate_goodData_isValid() throws Exception {
        assertThat(editPasswordData.validate(), equalTo(new String[]{}));
    }

    @Test
    public void validate_emptyOldPassword_returnsError() throws Exception {
        editPasswordData.setOldPassword("");
        assertThat(editPasswordData.validate(), equalTo(new String[]{EMPTY_OLD_PASSWORD}));
    }

    @Test
    public void validate_emptyNewPassword_returnsError() throws Exception {
        editPasswordData.setNewPassword("");
        assertThat(editPasswordData.validate(), equalTo(new String[]{EMPTY_NEW_PASSWORD}));
    }

    @Test
    public void validate_shortNewPassword_returnsError() throws Exception {
        editPasswordData.setNewPassword("12");
        assertThat(editPasswordData.validate(), equalTo(new String[]{SHORT_NEW_PASSWORD}));
    }
}