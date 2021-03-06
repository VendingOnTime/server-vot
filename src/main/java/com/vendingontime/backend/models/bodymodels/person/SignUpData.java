package com.vendingontime.backend.models.bodymodels.person;

import com.vendingontime.backend.models.person.PersonRole;
import com.vendingontime.backend.models.bodymodels.Validable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.vendingontime.backend.utils.StringUtils.*;

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
public class SignUpData implements Validable {
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MIN_PASSWORD_LENGTH = 5;

    public static final int MAX_USERNAME_LENGTH = 20;

    public static final String EMPTY_EMAIL = "EMPTY_EMAIL";
    public static final String EMPTY_USERNAME = "EMPTY_USERNAME";
    public static final String EMPTY_PASSWORD = "EMPTY_PASSWORD";
    public static final String EMPTY_DNI = "EMPTY_DNI";
    public static final String EMPTY_NAME = "EMPTY_NAME";
    public static final String EMPTY_SURNAMES = "EMPTY_SURNAMES";
    public static final String EMPTY_ROLE = "EMPTY_ROLE";

    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String INVALID_DNI = "INVALID_DNI";
    public static final String INVALID_USERNAME = "INVALID_USERNAME";
    public static final String INVALID_ROLE = "INVALID_ROLE";

    public static final String SHORT_USERNAME = "SHORT_USERNAME";
    public static final String SHORT_PASSWORD = "SHORT_PASSWORD";

    public static final String LONG_USERNAME = "LONG_USERNAME";

    private String email;
    private String username;
    private String password;
    private String dni;
    private String name;
    private String surnames;
    private PersonRole role;

    @Override
    public String[] validate() {
        List<String> causes = validateDependingOnRole();
        return causes.toArray(new String[causes.size()]);
    }

    private List<String> validateDependingOnRole() {
        if (role == null) return Collections.singletonList(EMPTY_ROLE);

        switch (role) {
            case CUSTOMER:
                return validateCustomer();
            case SUPERVISOR:
                return validateSupervisor();
            case TECHNICIAN:
                return validateTechnician();
            default:
                return Collections.singletonList(INVALID_ROLE);
        }
    }

    private List<String> validateCustomer() {
        return validateCommon();
    }

    private List<String> validateSupervisor() {
        List<String> causes = new LinkedList<>();
        causes.addAll(validateCommon());
        causes.addAll(validateBusinessCommon());
        return causes;
    }

    private List<String> validateTechnician() {
        List<String> causes = new LinkedList<>();
        causes.addAll(validateCommon());
        causes.addAll(validateBusinessCommon());
        if (isEmpty(dni)) causes.add(EMPTY_DNI);
        if (isEmpty(name)) causes.add(EMPTY_NAME);
        if (isEmpty(surnames)) causes.add(EMPTY_SURNAMES);
        return causes;
    }

    private List<String> validateBusinessCommon() {
        List<String> causes = new LinkedList<>();
        if (isEmpty(email)) causes.add(EMPTY_EMAIL);
        if (isEmpty(username)) causes.add(EMPTY_USERNAME);
        if (isEmpty(password)) causes.add(EMPTY_PASSWORD);
        return causes;
    }

    private List<String> validateCommon() {
        List<String> causes = new LinkedList<>();
        if (!isEmpty(email) && !isEmail(email)) causes.add(INVALID_EMAIL);
        if (!isEmpty(username) && !isValidUsername(username)) causes.add(INVALID_USERNAME);
        if (!isEmpty(username) && isShort(username, MIN_USERNAME_LENGTH)) causes.add(SHORT_USERNAME);
        if (!isEmpty(username) && isLong(username, MAX_USERNAME_LENGTH)) causes.add(LONG_USERNAME);
        if (!isEmpty(password) && isShort(password, MIN_PASSWORD_LENGTH)) causes.add(SHORT_PASSWORD);
        if (!isEmpty(dni) && !isDni(dni)) causes.add(INVALID_DNI);
        return causes;
    }

    public String getEmail() {
        return email;
    }

    public SignUpData setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SignUpData setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public SignUpData setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDni() {
        return dni;
    }

    public SignUpData setDni(String dni) {
        this.dni = dni;
        return this;
    }

    public String getName() {
        return name;
    }

    public SignUpData setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurnames() {
        return surnames;
    }

    public SignUpData setSurnames(String surnames) {
        this.surnames = surnames;
        return this;
    }

    public PersonRole getRole() {
        return role;
    }

    public SignUpData setRole(PersonRole role) {
        this.role = role;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignUpData)) return false;

        SignUpData that = (SignUpData) o;

        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        if (getDni() != null ? !getDni().equals(that.getDni()) : that.getDni() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getSurnames() != null ? !getSurnames().equals(that.getSurnames()) : that.getSurnames() != null)
            return false;
        return getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        int result = getEmail() != null ? getEmail().hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getDni() != null ? getDni().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getSurnames() != null ? getSurnames().hashCode() : 0);
        result = 31 * result + (getRole() != null ? getRole().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SignUpData{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", surnames='" + surnames + '\'' +
                ", role=" + role +
                '}';
    }
}
