package integration.com.vendingontime.backend.testutils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vendingontime.backend.config.inject.ConfigModule;
import org.junit.Before;

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
public abstract class IntegrationTest {
    private static final Injector injector = Guice.createInjector(new ConfigModule());

    @Before
    public void injectContext() throws Exception {
        injector.injectMembers(this);
    }
}
