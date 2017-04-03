package com.vendingontime.backend.models.machine;

import com.vendingontime.backend.models.AbstractEntity;

import javax.persistence.*;

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
@Entity
public class Machine extends AbstractEntity<Machine> {
    @OneToOne(optional = false) private MachineLocation location; // Minimo 2, max 140
    @Column @Enumerated private MachineType type;  // --
    @Column @Enumerated private MachineState state; // --
    @Column private String description; // Minimo 0, max 300

    public Machine() {
        super();
    }

    // TODO Constructor with NewMachineData

    public void update(Machine machine) {
        this.location = machine.getLocation();
        this.type = machine.getType();
        this.state = machine.getState();
        this.description = machine.getDescription();
    }

    public MachineLocation getLocation() {
        return location;
    }

    public Machine setLocation(MachineLocation machineLocation) {
        this.location = machineLocation;
        return this;
    }

    public MachineType getType() {
        return type;
    }

    public Machine setType(MachineType machineType) {
        this.type = machineType;
        return this;
    }

    public MachineState getState() {
        return state;
    }

    public Machine setState(MachineState machineState) {
        this.state = machineState;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Machine setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Machine machine = (Machine) o;

        if (getLocation() != null ? !getLocation().equals(machine.getLocation()) : machine.getLocation() != null)
            return false;
        if (getType() != machine.getType()) return false;
        if (getState() != machine.getState()) return false;
        return getDescription() != null ? getDescription().equals(machine.getDescription()) : machine.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "location=" + location +
                ", type=" + type +
                ", state=" + state +
                ", description='" + description + '\'' +
                '}';
    }
}
