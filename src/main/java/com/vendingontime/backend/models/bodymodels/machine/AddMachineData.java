package com.vendingontime.backend.models.bodymodels.machine;

import com.vendingontime.backend.models.bodymodels.Validable;
import com.vendingontime.backend.models.location.MachineLocation;
import com.vendingontime.backend.models.machine.MachineState;
import com.vendingontime.backend.models.machine.MachineType;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonRole;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
public class AddMachineData implements Validable {
    public static final int MIN_MACHINE_LOCATION_NAME_LENGTH  = 4;
    public static final int MAX_MACHINE_LOCATION_NAME_LENGTH  = 140;

    public static final int MIN_MACHINE_DESCRIPTION_LENGTH  = 0;
    public static final int MAX_MACHINE_DESCRIPTION_LENGTH  = 300;

    public static final String EMPTY_LOCATION_NAME = "EMPTY_LOCATION_NAME";

    public static final String INVALID_MACHINE_DESCRIPTION = "INVALID_MACHINE_DESCRIPTION";
    public static final String INVALID_MACHINE_LOCATION = "INVALID_MACHINE_LOCATION";
    public static final String INVALID_MACHINE_LOCATION_NAME = "INVALID_MACHINE_LOCATION_NAME";
    public static final String INVALID_MACHINE_TYPE = "INVALID_MACHINE_TYPE";
    public static final String INVALID_MACHINE_STATE = "INVALID_MACHINE_STATE";

    public static final String SHORT_MACHINE_LOCATION_NAME = "SHORT_MACHINE_LOCATION_NAME";

    public static final String LONG_MACHINE_LOCATION_NAME = "LONG_MACHINE_LOCATION_NAME";
    public static final String LONG_MACHINE_DESCRIPTION = "LONG_MACHINE_DESCRIPTION";

    private MachineLocation machineLocation;
    private MachineType machineType;
    private MachineState machineState;
    private String description;
    private Person requester;

    public boolean requesterIsAuthorized() {
        if (requester == null) return false;
        if (requester.getRole() != PersonRole.SUPERVISOR) return false;
        if (requester.getCompany() == null) return false;

        return true;
    }

    @Override
    public String[] validate() {
        List<String> causes = new LinkedList<>();

        causes.addAll(validateMachineLocation());
        causes.addAll(validateMachineDescription());

        return causes.toArray(new String[causes.size()]);
    }

    private List<String> validateMachineLocation() {
        if (machineLocation == null) return Collections.singletonList(INVALID_MACHINE_LOCATION);
        if (machineLocation.getName() == null) return Collections.singletonList(INVALID_MACHINE_LOCATION_NAME);

        List<String> causes = new LinkedList<>();
        if (machineLocation.getName().length() < MIN_MACHINE_LOCATION_NAME_LENGTH) {
            causes.add(SHORT_MACHINE_LOCATION_NAME);
        }

        if (machineLocation.getName().length() > MAX_MACHINE_LOCATION_NAME_LENGTH) {
            causes.add(LONG_MACHINE_LOCATION_NAME);
        }

        return causes;
    }

    private List<String> validateMachineDescription() {
        if (description == null) return Collections.singletonList(INVALID_MACHINE_DESCRIPTION);

        List<String> causes = new LinkedList<>();
        if (description.length() > MAX_MACHINE_DESCRIPTION_LENGTH) {
            causes.add(LONG_MACHINE_DESCRIPTION);
        }

        return causes;
    }


    public MachineLocation getMachineLocation() {
        return machineLocation;
    }

    public AddMachineData setMachineLocation(MachineLocation machineLocation) {
        this.machineLocation = machineLocation;
        return this;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    public AddMachineData setMachineType(MachineType machineType) {
        this.machineType = machineType;
        return this;
    }

    public MachineState getMachineState() {
        return machineState;
    }

    public AddMachineData setMachineState(MachineState machineState) {
        this.machineState = machineState;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AddMachineData setDescription(String description) {
        this.description = description;
        return this;
    }

    public Person getRequester() {
        return requester;
    }

    public AddMachineData setRequester(Person requester) {
        this.requester = requester;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddMachineData that = (AddMachineData) o;

        if (getMachineLocation() != null ? !getMachineLocation().equals(that.getMachineLocation()) : that.getMachineLocation() != null)
            return false;
        if (getMachineType() != that.getMachineType()) return false;
        if (getMachineState() != that.getMachineState()) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        return getRequester() != null ? getRequester().equals(that.getRequester()) : that.getRequester() == null;
    }

    @Override
    public int hashCode() {
        int result = getMachineLocation() != null ? getMachineLocation().hashCode() : 0;
        result = 31 * result + (getMachineType() != null ? getMachineType().hashCode() : 0);
        result = 31 * result + (getMachineState() != null ? getMachineState().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getRequester() != null ? getRequester().hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "AddMachineData{" +
                "machineLocation=" + machineLocation +
                ", machineType=" + machineType +
                ", machineState=" + machineState +
                ", description='" + description + '\'' +
                ", requester=" + requester +
                '}';
    }
}
