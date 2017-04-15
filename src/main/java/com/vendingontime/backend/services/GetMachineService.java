package com.vendingontime.backend.services;

import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;

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

    public Optional<Machine> getDataFrom(String machineId, Person person) {
        if (!isValidPerson(person)) return Optional.empty();

        Optional<Machine> possibleMachine = repository.findById(machineId);
        if (!possibleMachine.isPresent()) return Optional.empty();

        Machine foundMachine = possibleMachine.get();
        if (!foundMachine.getCompany().equals(person.getCompany())) return Optional.empty();

        return possibleMachine;
    }

    private boolean isValidPerson(Person person) {
        if (person == null) return false;
        if (person.getId() == null || person.getId().isEmpty()) return false;
        if (person.getCompany() == null) return false;
        if (person.getCompany().getId() == null || person.getCompany().getId().isEmpty()) return false;

        return true;
    }
}
