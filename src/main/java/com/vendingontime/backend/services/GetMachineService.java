package com.vendingontime.backend.services;

import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import javax.inject.Inject;
import java.util.Optional;

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

public class GetMachineService extends AbstractService {
    private MachineRepository repository;

    @Inject
    public GetMachineService(MachineRepository repository) {
        this.repository = repository;
    }

    public Optional<Machine> getDataFrom(String machineId, Person requester) {
        if (!isAuthorized(requester)) throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        Optional<Machine> possibleMachine = repository.findById(machineId);
        if (!possibleMachine.isPresent()) return Optional.empty();

        Machine foundMachine = possibleMachine.get();
        if (!foundMachine.getCompany().equals(requester.getCompany())) throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        return possibleMachine;
    }

    private boolean isAuthorized(Person requester) {
        if (requester == null) return false;
        if (requester.getId() == null || requester.getId().isEmpty()) return false;
        if (requester.getCompany() == null) return false;
        if (requester.getCompany().getId() == null || requester.getCompany().getId().isEmpty()) return false;

        return true;
    }
}