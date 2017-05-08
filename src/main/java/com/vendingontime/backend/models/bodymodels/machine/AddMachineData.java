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
    public static final int MIN_LOCATION_NAME_LENGTH = 4;
    public static final int MAX_LOCATION_NAME_LENGTH = 140;

    public static final int MIN_DESCRIPTION_LENGTH = 0;
    public static final int MAX_DESCRIPTION_LENGTH = 300;

    public static final String EMPTY_LOCATION_NAME = "EMPTY_LOCATION_NAME";

    public static final String INVALID_DESCRIPTION = "INVALID_DESCRIPTION";
    public static final String INVALID_LOCATION = "INVALID_LOCATION";
    public static final String INVALID_LOCATION_NAME = "INVALID_LOCATION_NAME";
    public static final String INVALID_TYPE = "INVALID_TYPE";
    public static final String INVALID_STATE = "INVALID_STATE";

    public static final String SHORT_LOCATION_NAME = "SHORT_LOCATION_NAME";

    public static final String LONG_LOCATION_NAME = "LONG_LOCATION_NAME";
    public static final String LONG_DESCRIPTION = "LONG_DESCRIPTION";

    private MachineLocation location;
    private MachineType type;
    private MachineState state;
    private String description;
    private Person requester;

    // FIXME: 2/5/17 Part of this business logic should be moved to the service
    public boolean requesterIsAuthorized() {
        if (requester == null || requester.getId() == null) return false;
        if (requester.getRole() != PersonRole.SUPERVISOR) return false;
        if (requester.getCompany() == null || requester.getCompany().getId() == null) return false;

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
        if (location == null) return Collections.singletonList(INVALID_LOCATION);
        if (location.getName() == null) return Collections.singletonList(INVALID_LOCATION_NAME);

        List<String> causes = new LinkedList<>();
        if (location.getName().length() < MIN_LOCATION_NAME_LENGTH) {
            causes.add(SHORT_LOCATION_NAME);
        }

        if (location.getName().length() > MAX_LOCATION_NAME_LENGTH) {
            causes.add(LONG_LOCATION_NAME);
        }

        return causes;
    }

    private List<String> validateMachineDescription() {
        if (description == null) return Collections.singletonList(INVALID_DESCRIPTION);

        List<String> causes = new LinkedList<>();
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            causes.add(LONG_DESCRIPTION);
        }

        return causes;
    }


    public MachineLocation getLocation() {
        return location;
    }

    public AddMachineData setLocation(MachineLocation location) {
        this.location = location;
        return this;
    }

    public MachineType getType() {
        return type;
    }

    public AddMachineData setType(MachineType type) {
        this.type = type;
        return this;
    }

    public MachineState getState() {
        return state;
    }

    public AddMachineData setState(MachineState state) {
        this.state = state;
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
        if (!(o instanceof AddMachineData)) return false;

        AddMachineData that = (AddMachineData) o;

        if (getLocation() != null ? !getLocation().equals(that.getLocation()) : that.getLocation() != null)
            return false;
        if (getType() != that.getType()) return false;
        if (getState() != that.getState()) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        return getRequester() != null ? getRequester().equals(that.getRequester()) : that.getRequester() == null;
    }

    @Override
    public int hashCode() {
        int result = getLocation() != null ? getLocation().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getRequester() != null ? getRequester().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddMachineData{" +
                "location=" + location +
                ", type=" + type +
                ", state=" + state +
                ", description='" + description + '\'' +
                ", requester=" + requester +
                '}';
    }
}
