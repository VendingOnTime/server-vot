package com.vendingontime.backend.services;
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

import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.PersonRepository;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import java.util.Optional;

public class EditPersonService extends AbstractService {

    private final AuthProvider authProvider;
    private final PersonRepository repository;

    @Inject
    public EditPersonService(AuthProvider authProvider, PersonRepository repository) {
        this.authProvider = authProvider;
        this.repository = repository;
    }

    public Optional<Person> updatePerson(EditPersonData editPersonData) throws BusinessLogicException {
        String[] validationErrors = editPersonData.validate();
        if(validationErrors.length != 0) throw new BusinessLogicException(validationErrors);

        Optional<Person> personById = repository.findById(editPersonData.getId());
        if (!personById.isPresent()) return personById;

        Person requester = editPersonData.getRequester();
        Person person = personById.get();

        if (!authProvider.canModify(requester, person))
            throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        person.updateWith(editPersonData);
        return repository.update(person);
    }
}
