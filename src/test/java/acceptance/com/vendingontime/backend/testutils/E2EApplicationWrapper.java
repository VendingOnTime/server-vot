package acceptance.com.vendingontime.backend.testutils;

import com.vendingontime.backend.Application;

import java.util.concurrent.atomic.AtomicInteger;

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

public class E2EApplicationWrapper {
    private volatile static E2EApplicationWrapper instance;

    private final Application application;
    private final AtomicInteger counter;

    private E2EApplicationWrapper() {
        application = new Application();
        counter = new AtomicInteger();
    }

    public static E2EApplicationWrapper getInstance() {
        if (instance == null) {
            synchronized (E2EApplicationWrapper.class) {
                if (instance == null) {
                    instance = new E2EApplicationWrapper();
                }
            }
        }
        return instance;
    }

    public void start() {
        int result = counter.getAndIncrement();
        if (result == 0) {
            application.start();
        }
    }

    public void stop() {
        int result = counter.decrementAndGet();
        if (result == 0) {
            application.stop();
        }
    }
}
