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

import com.vendingontime.backend.models.bodymodels.machine.EditMachineData;
import com.vendingontime.backend.models.company.Company;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import javax.inject.Inject;
import java.util.Optional;

public class EditMachineService extends AbstractService {
    private MachineRepository repository;

    @Inject
    public EditMachineService(MachineRepository machineRepository) {
        this.repository = machineRepository;
    }

    public Optional<Machine> updateMachine(EditMachineData machineUpdateCandidate) {
        BusinessLogicException insufficientPermissionsException =
                new BusinessLogicException(new String[]{INSUFFICIENT_PERMISSIONS});

        if (!machineUpdateCandidate.requesterIsAuthorized()) throw insufficientPermissionsException;

        String[] signUpErrors = machineUpdateCandidate.validate();
        if(signUpErrors.length != 0) throw new BusinessLogicException(signUpErrors);

        Optional<Machine> machineById = repository.findById(machineUpdateCandidate.getId());
        if (!machineById.isPresent()) return machineById;

        Machine machine = machineById.get();
        Company requesterCompany = machineUpdateCandidate.getRequester().getCompany();

        if (!machine.getCompany().equals(requesterCompany)) throw insufficientPermissionsException;

        machine.update(new Machine(machineUpdateCandidate));
        return repository.update(machine);
    }
}
