package com.vendingontime.backend.initializers;

import com.google.inject.Inject;
import com.vendingontime.backend.RESTContext;
import com.vendingontime.backend.routes.SparkRouter;

import java.util.Set;

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
public class RouteInitializer {

    private final RESTContext context;
    private final Set<SparkRouter> routers;

    @Inject
    public RouteInitializer(RESTContext context, Set<SparkRouter> routers) {
        this.context = context;
        this.routers = routers;
    }

    public void setUp() {
        for (SparkRouter router : routers) {
            context.addRouter(router);
        }
    }
}
