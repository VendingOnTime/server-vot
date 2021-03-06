package com.vendingontime.backend.services;

import com.google.inject.Inject;
import com.vendingontime.backend.models.bodymodels.machine.AssignMaintainerData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.repositories.PersonRepository;
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
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class AssignMaintainerService extends AbstractService {

    private final PersonRepository personRepository;
    private final MachineRepository machineRepository;
    private final AuthProvider authProvider;

    @Inject
    public AssignMaintainerService(PersonRepository personRepository, MachineRepository machineRepository, AuthProvider authProvider) {
        this.personRepository = personRepository;
        this.machineRepository = machineRepository;
        this.authProvider = authProvider;
    }

    public Optional<Machine> assignMaintainer(AssignMaintainerData assignMaintainerData) throws BusinessLogicException {
        String[] validateErrors = assignMaintainerData.validate();
        if (validateErrors.length != 0)
            throw new BusinessLogicException(validateErrors);

        Optional<Machine> machineById = machineRepository.findById(assignMaintainerData.getId());
        if (!machineById.isPresent())
            return machineById;

        Machine machine = machineById.get();
        if (!authProvider.canModify(assignMaintainerData.getRequester(), machine))
            throw new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        Optional<Person> technicianById = personRepository.findById(assignMaintainerData.getTechnicianId());
        if (!technicianById.isPresent())
            return Optional.empty();

        Person technician = technicianById.get();
        technician.addMaintainedMachine(machine);
        Optional<Person> possibleUpdated = personRepository.update(technician);

        return possibleUpdated.isPresent() ? Optional.of(machine) : Optional.empty();
    }
}
