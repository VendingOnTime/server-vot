package unit.com.vendingontime.backend.routes.utils;

import com.vendingontime.backend.routes.utils.RESTResult;
import com.vendingontime.backend.routes.utils.RESTResultFactory;
import com.vendingontime.backend.routes.utils.ResultFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
public class RESTResultFactoryTest {

    private ResultFactory resultFactory;

    @Before
    public void setUp() throws Exception {
        resultFactory = new RESTResultFactory();
    }

    @After
    public void tearDown() throws Exception {
        resultFactory = null;
    }

    @Test
    public void ok() throws Exception {
        String data = "OK";
        RESTResult ok = (RESTResult) resultFactory.ok(data);
        assertTrue(ok.getSuccess());
        assertEquals(data, ok.getData());
        assertNull(ok.getError());
    }

    @Test
    public void error() throws Exception {
        String cause = "ERROR";
        RESTResult error = (RESTResult) resultFactory.error(cause);
        assertFalse(error.getSuccess());
        assertNull(error.getData());
        assertEquals(cause, error.getError());
    }

}