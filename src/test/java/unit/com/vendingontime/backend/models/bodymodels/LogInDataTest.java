package unit.com.vendingontime.backend.models.bodymodels;

import com.vendingontime.backend.models.bodymodels.person.LogInData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static org.junit.Assert.*;
import static com.vendingontime.backend.models.bodymodels.person.LogInData.*;

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
public class LogInDataTest {
    private LogInData user;

    @Before
    public void setUp() throws Exception {
        user = FixtureFactory.generateLogInData();
    }

    @After
    public void tearDown() throws Exception {
        user = null;
    }

    @Test
    public void validate_user_noErrors() {
        assertArrayEquals(new String[]{}, user.validate());
    }

    @Test
    public void validate_invalidMail_error() throws Exception {
        user.setEmail("aaaa");
        assertArrayEquals(new String[]{BAD_LOGIN}, user.validate());
    }

    @Test
    public void validate_invalidPassword_error() throws Exception {
        user.setPassword("");
        assertArrayEquals(new String[]{BAD_LOGIN}, user.validate());
    }

    @Test
    public void validate_user_allErrors() throws Exception {
        user.setEmail("aaaa").setPassword("");
        assertArrayEquals(new String[]{BAD_LOGIN}, user.validate());
    }

}