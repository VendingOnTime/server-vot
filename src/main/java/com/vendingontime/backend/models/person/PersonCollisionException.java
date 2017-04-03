package com.vendingontime.backend.models.person;

import java.util.Arrays;
import java.util.stream.Collectors;

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
public class PersonCollisionException extends RuntimeException {
    public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
    public static final String USERNAME_EXISTS = "USERNAME_EXISTS";
    public static final String DNI_EXISTS = "DNI_EXISTS";

    private final String[] causes;

    public PersonCollisionException(Cause[] errors) {
        super();
        this.causes = Arrays.stream(errors)
                .map(error -> error.cause)
                .collect(Collectors.toList())
                .toArray(new String[errors.length]);
    }

    public enum Cause {
        EMAIL(EMAIL_EXISTS),
        USERNAME(USERNAME_EXISTS),
        DNI(DNI_EXISTS);

        private final String cause;

        Cause(String cause) {
            this.cause = cause;
        }
    }

    public String[] getCauses() {
        return causes;
    }
}
