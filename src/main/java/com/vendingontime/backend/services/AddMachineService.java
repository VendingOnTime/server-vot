package com.vendingontime.backend.services;

import com.vendingontime.backend.models.bodymodels.machine.AddMachineData;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.repositories.MachineRepository;
import com.vendingontime.backend.services.utils.BusinessLogicException;

import javax.inject.Inject;

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

public class AddMachineService {
    private MachineRepository repository;

    @Inject
    public AddMachineService(MachineRepository machineRepository) {
        this.repository = machineRepository;
    }

    public Machine createMachine(AddMachineData machineCandidate) {
        String[] signUpErrors = machineCandidate.validate();

        if(signUpErrors.length != 0) {
            throw new BusinessLogicException(signUpErrors);
        }

        Machine machine = new Machine(machineCandidate);

        machine = repository.create(machine);

        return machine;
    }
}
