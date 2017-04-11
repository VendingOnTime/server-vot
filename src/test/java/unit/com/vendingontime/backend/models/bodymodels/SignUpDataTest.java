package unit.com.vendingontime.backend.models.bodymodels;

import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.person.SignUpData.*;
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
public class SignUpDataTest {
    private SignUpData customer;
    private SignUpData supervisor;
    private SignUpData technician;

    @Before
    public void setUp() throws Exception {
        customer = FixtureFactory.generateSignUpData().setRole(PersonRole.CUSTOMER);
        supervisor = FixtureFactory.generateSignUpData().setRole(PersonRole.SUPERVISOR);
        technician = FixtureFactory.generateSignUpData().setRole(PersonRole.TECHNICIAN);
    }

    @After
    public void tearDown() throws Exception {
        customer = null;
        supervisor = null;
        technician = null;
    }

    @Test
    public void validate_noRole_error() throws Exception {
        assertArrayEquals(new String[]{EMPTY_ROLE}, new SignUpData().validate());
    }

    @Test
    public void validate_customer_noErrors() throws Exception {
        assertArrayEquals(new String[]{}, customer.validate());
    }

    @Test
    public void validate_user_invalidEmail() throws Exception {
        customer.setEmail("aaaa");
        assertArrayEquals(new String[]{INVALID_EMAIL}, customer.validate());
    }

    @Test
    public void validate_user_invalidDni() throws Exception {
        customer.setDni("aaaa");
        assertArrayEquals(new String[]{INVALID_DNI}, customer.validate());
    }

    @Test
    public void validate_user_invalidUsername() throws Exception {
        customer.setUsername("user@");
        assertArrayEquals(new String[]{INVALID_USERNAME}, customer.validate());
    }

    @Test
    public void validate_user_shortPassword() throws Exception {
        customer.setPassword(StringUtils.createFilled(MIN_PASSWORD_LENGTH - 1));
        assertArrayEquals(new String[]{SHORT_PASSWORD}, customer.validate());
    }

    @Test
    public void validate_user_shortUsername() throws Exception {
        customer.setUsername(StringUtils.createFilled(MIN_USERNAME_LENGTH - 1));
        assertArrayEquals(new String[]{SHORT_USERNAME}, customer.validate());
    }

    @Test
    public void validate_user_longUsername() throws Exception {
        customer.setUsername(StringUtils.createFilled(MAX_USERNAME_LENGTH + 1));
        assertArrayEquals(new String[]{LONG_USERNAME}, customer.validate());
    }

    @Test
    public void validate_supervisor_noErrors() throws Exception {
        assertArrayEquals(new String[]{}, supervisor.validate());
    }

    @Test
    public void validate_supervisor_allErrors() throws Exception {
        SignUpData supervisor = new SignUpData();
        supervisor.setRole(PersonRole.SUPERVISOR);

        assertArrayEquals(new String[]{
                EMPTY_EMAIL, EMPTY_USERNAME, EMPTY_PASSWORD
        }, supervisor.validate());
    }

    @Test
    public void validate_technician_noErrors() throws Exception {
        assertArrayEquals(new String[]{}, technician.validate());
    }

    @Test
    public void validate_technician_allErrors() throws Exception {
        SignUpData technician = new SignUpData();
        technician.setRole(PersonRole.TECHNICIAN);

        assertArrayEquals(new String[]{
                EMPTY_EMAIL, EMPTY_USERNAME, EMPTY_PASSWORD, EMPTY_DNI, EMPTY_NAME, EMPTY_SURNAMES
        }, technician.validate());
    }
}