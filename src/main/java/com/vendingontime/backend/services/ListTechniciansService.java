package com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import java.util.List;

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
public class ListTechniciansService extends AbstractService {
    private PersonRepository repository;

    @Inject
    public ListTechniciansService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> listFor(Person person) throws BusinessLogicException {
        if (person == null) throw new NullPointerException("person");
        if (person.getRole() == null) throw new NullPointerException("a person needs a role");

        switch (person.getRole()) {
            case SUPERVISOR:
                return repository.findTechniciansByCompany(person.getOwnedCompany());
            default:
                throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});
        }
    }
}
