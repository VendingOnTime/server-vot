package com.vendingontime.backend.services.utils;

import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;

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
public class AuthProviderImpl implements AuthProvider {

    @Override
    public boolean canModify(Person requester, AbstractEntity entity) {
        if (oneIsNull(requester, entity)) return false;

        if (equalsClass(entity, Person.class)) return canModify(requester, (Person) entity);
        if (equalsClass(entity, Company.class)) return canModify(requester, (Company) entity);
        if (equalsClass(entity, Machine.class)) return canModify(requester, (Machine) entity);

        return false;
    }

    @Override
    public boolean canModifyPassword(Person requester, Person person) {
        if (oneIsNull(requester)) return false;
        return requester.equals(person);
    }

    private boolean canModify(Person requester, Person person) {
        if (requester.equals(person)) return true;
        if (oneIsNull(person.getCompany())) return false;
        return person.getCompany().equals(requester.getOwnedCompany());
    }

    private boolean canModify(Person requester, Company company) {
        return company.equals(requester.getOwnedCompany());
    }

    private boolean canModify(Person requester, Machine machine) {
        if (oneIsNull(machine.getCompany())) return false;
        return machine.getCompany().equals(requester.getOwnedCompany());
    }

    @Override
    public boolean canAccess(Person requester, AbstractEntity entity) {
        if (oneIsNull(requester, entity)) return false;

        if (equalsClass(entity, Machine.class)) return canSee(requester, (Machine) entity);
        if (equalsClass(entity, Person.class)) return  canSee(requester, (Person) entity);

        return false;
    }

    private boolean canSee(Person requester, Machine machine) {
        if (oneIsNull(requester.getCompany())) return false;
        return requester.getCompany().equals(machine.getCompany());
    }

    private boolean canSee(Person requester, Person person) {
        if (requester.equals(person)) return true;
        if (oneIsNull(requester.getCompany())) return false;
        // TODO: 15/05/2017 Decide if a technician should be able to see the profile of his supervisor
        return requester.getCompany().equals(person.getCompany());
    }

    // Utility methods

    private boolean oneIsNull(Object ...objects) {
        for (Object object : objects) {
            if (object == null) return true;
        }
        return false;
    }

    private boolean equalsClass(Object object, Class aClass) {
        return object.getClass().equals(aClass);
    }
}
