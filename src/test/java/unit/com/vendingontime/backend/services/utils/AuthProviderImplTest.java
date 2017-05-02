package unit.com.vendingontime.backend.services.utils;

import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.services.utils.AuthProviderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

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

public class AuthProviderImplTest {

    private AuthProviderImpl authProvider;

    private Person requester;

    @Before
    public void setUp() throws Exception {
        authProvider = new AuthProviderImpl();
        requester = FixtureFactory.generateSupervisor();
    }

    @After
    public void tearDown() throws Exception {
        authProvider = null;
        requester = null;
    }

    @Test
    public void canModify_himself_isTrue() throws Exception {
        assertThat(authProvider.canModify(requester, requester), is(true));
    }

    @Test
    public void canModify_nonRelatedOne_isFalse() throws Exception {
        assertThat(authProvider.canModify(requester, FixtureFactory.generateCustomer()), is(false));
    }
}