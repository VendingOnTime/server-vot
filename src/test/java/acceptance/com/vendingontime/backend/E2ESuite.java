package acceptance.com.vendingontime.backend;

import com.vendingontime.backend.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import spark.Spark;


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

// TODO Add extra E2E tests here to ensure that they run with a server running

@RunWith(Suite.class)
@Suite.SuiteClasses({
        E2ESignUpSupervisorTest.class,
        E2ELogInTest.class,
        E2EUserProfileTest.class,
        E2EAddMachineTest.class
})
public class E2ESuite {

    @BeforeClass
    public static void setUp() throws Exception {
        Application.main(null);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Spark.stop();
    }
}
