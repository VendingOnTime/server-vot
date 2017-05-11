package com.vendingontime.backend.services.utils;
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

import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;

public class AuthProviderImpl implements AuthProvider {

    @Override
    public boolean canModify(Person requester, Person person) {
        // TODO: alberto@2/5/17 Once company person hierarchy gets more complex add extra checks here
        return requester.equals(person);
    }

    @Override
    public boolean canModifyPassword(Person requester, Person person) {
        return requester.equals(person);
    }

    @Override
    public boolean canModify(Person requester, Company company) {
        if (company == null) return false;
        return company.equals(requester.getOwnedCompany());
    }

    @Override
    public boolean canModify(Person requester, Machine machine) {
        if (machine == null || machine.getCompany() == null) return false;
        return machine.getCompany().equals(requester.getOwnedCompany());
    }
}
