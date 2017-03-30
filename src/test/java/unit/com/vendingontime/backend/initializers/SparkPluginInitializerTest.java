package unit.com.vendingontime.backend.initializers;

import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.initializers.RouteInitializer;
import com.vendingontime.backend.initializers.SparkPluginInitializer;
import com.vendingontime.backend.initializers.sparkplugins.SparkPlugin;
import com.vendingontime.backend.routes.SparkRouter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
public class SparkPluginInitializerTest {
    private SparkPluginInitializer initializer;
    private RESTContext context;

    @Before
    public void setUp() throws Exception {
        context = mock(RESTContext.class);
        SparkPlugin testRouter1 = mock(SparkPlugin.class);
        SparkPlugin testRouter2 = mock(SparkPlugin.class);

        HashSet<SparkPlugin> plugins = new HashSet<>();
        plugins.add(testRouter1);
        plugins.add(testRouter2);

        initializer = new SparkPluginInitializer(context, plugins);
    }

    @After
    public void tearDown() throws Exception {
        initializer = null;
        context = null;
    }

    @Test
    public void setUpCallsConfigureOnRoutes() throws Exception {
        initializer.setUp();

        verify(context, times(2)).addPlugin(any());
    }
}