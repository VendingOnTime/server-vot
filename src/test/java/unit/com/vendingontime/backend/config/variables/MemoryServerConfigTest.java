package unit.com.vendingontime.backend.config.variables;

import com.vendingontime.backend.config.variables.MemoryServerConfig;
import com.vendingontime.backend.config.variables.ServerVariable;
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
public class MemoryServerConfigTest {
    private MemoryServerConfig config;

    @Before
    public void setUp() throws Exception {
        config = new MemoryServerConfig();
    }

    @After
    public void tearDown() throws Exception {
        config = null;
    }

    @Test
    public void getString() throws Exception {
        assertEquals(ServerVariable.ENV.getDefaultValue(), config.getString(ServerVariable.ENV));
    }

    @Test
    public void getInt() throws Exception {
        assertEquals(Integer.parseInt(ServerVariable.PORT.getDefaultValue()), config.getInt(ServerVariable.PORT));
    }

    @Test
    public void setValue() throws Exception {
        int PORT = 80;
        config.setVariable(ServerVariable.PORT, String.valueOf(PORT));
        assertEquals(PORT, config.getInt(ServerVariable.PORT));
    }

}