package com.vendingontime.backend.services;

import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.bodymodels.PersonRequest;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.Repository;
import com.vendingontime.backend.services.utils.AuthProvider;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import java.util.Optional;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public abstract class AbstractGetService<MODEL extends AbstractEntity> extends AbstractService {
    private final Repository<MODEL> repository;
    private final AuthProvider authProvider;

    public AbstractGetService(Repository<MODEL> repository, AuthProvider authProvider) {
        this.repository = repository;
        this.authProvider = authProvider;
    }

    public Optional<MODEL> getBy(PersonRequest personRequest) throws BusinessLogicException {
        validateInput(personRequest);

        Optional<MODEL> possibleEntity = repository.findById(personRequest.getId());
        if (!possibleEntity.isPresent()) return Optional.empty();

        Person requester = personRequest.getRequester();
        MODEL entity = possibleEntity.get();

        if (!authProvider.canSee(requester, entity))
            throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        return possibleEntity;
    }
}
