package com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.bodymodels.Validable;

import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.utils.StringUtils.isEmpty;
import static com.vendingontime.backend.utils.StringUtils.isEmail;

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
public class LogInData implements Validable {
    public static final String BAD_LOGIN = "BAD_LOGIN";

    private String email;
    private String password;

    @Override
    public String[] validate() {
        List<String> causes = new LinkedList<>();

        if (isEmpty(email) || !isEmail(email)) causes.add(BAD_LOGIN);
        else if (isEmpty(password)) causes.add(BAD_LOGIN);

        return causes.toArray(new String[causes.size()]);
    }

    public String getEmail() {
        return email;
    }

    public LogInData setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LogInData setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogInData that = (LogInData) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "SignUpData{" +
                "email='" + email +
                '}';
    }
}
