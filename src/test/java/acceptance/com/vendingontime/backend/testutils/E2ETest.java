package acceptance.com.vendingontime.backend.testutils;

import com.vendingontime.backend.config.variables.MemoryServerConfig;
import com.vendingontime.backend.config.variables.ServerVariable;
import integration.com.vendingontime.backend.testutils.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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
public abstract class E2ETest extends IntegrationTest {
    protected static final String host = "http://localhost:"
            + new MemoryServerConfig().getString(ServerVariable.PORT);

    @BeforeClass
    public static void setUp() throws Exception {
        E2EApplicationWrapper.getInstance().start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        E2EApplicationWrapper.getInstance().stop();
    }
}
