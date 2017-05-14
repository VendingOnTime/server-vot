package unit.com.vendingontime.backend.models.bodymodels;

import com.vendingontime.backend.models.bodymodels.RemovalRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testutils.FixtureFactory;

import static com.vendingontime.backend.models.bodymodels.RemovalRequest.EMPTY_ID;
import static com.vendingontime.backend.models.bodymodels.RemovalRequest.EMPTY_REQUESTER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class RemovalRequestTest {
    
    private static final String ENTITY_ID = "ENTITY_ID";
    
    private RemovalRequest removalRequest;

    @Before
    public void setUp() throws Exception {
        removalRequest = new RemovalRequest()
                .setId(ENTITY_ID)
                .setRequester(FixtureFactory.generateSupervisor());
    }

    @After
    public void tearDown() throws Exception {
        removalRequest = null;
    }

    @Test
    public void validate_withValidData_returnsEmpty() throws Exception {
        assertThat(removalRequest.validate(), is(new String[]{}));
    }

    @Test
    public void validate_withEmptyId_returnsError() throws Exception {
        removalRequest.setId("");
        assertThat(removalRequest.validate(), is(new String[]{EMPTY_ID}));
    }

    @Test
    public void validate_withEmptyRequester_returnsError() throws Exception {
        removalRequest.setRequester(null);
        assertThat(removalRequest.validate(), is(new String[]{EMPTY_REQUESTER}));
    }

    @Test
    public void validate_allInvalid_returnsErrors() throws Exception {
        removalRequest.setId("");
        removalRequest.setRequester(null);
        assertThat(removalRequest.validate(), is(new String[]{EMPTY_ID, EMPTY_REQUESTER}));
    }
}