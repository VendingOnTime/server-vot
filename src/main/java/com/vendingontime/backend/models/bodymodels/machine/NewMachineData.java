package com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.models.bodymodels.Validable;
import com.vendingontime.backend.models.machine.MachineLocation;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;

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
public class NewMachineData implements Validable {
    private MachineLocation machineLocation; // Minimo 2, max 140
    private MachineType machineType;  // --
    private MachineState machineState; // --
    private String description; // Minimo 0, max 300

    @Override
    public String[] validate() {
        return null;
    }


}
