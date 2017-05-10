package com.vendingontime.backend.models.company;
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

import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.machine.Machine;
import com.vendingontime.backend.models.person.Person;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "Company.findById", query = "SELECT c FROM Company c WHERE c.id = :id AND c.disabled = false")
})
public class Company extends AbstractEntity<Company> {

    @OneToOne(mappedBy = "ownedCompany", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Person owner;

    @OneToMany(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Machine> machines = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Person> technicians = new HashSet<>();

    public Company() {
        super();
    }

    @Override
    public void updateWith(Company company) {
        this.owner = company.getOwner();
        this.machines = company.getMachines();
        this.technicians = company.getTechnicians();
    }

    public Person getOwner() {
        return owner;
    }

    public Company setOwner(Person owner) {
        this.owner = owner;
        owner.setOwnedCompany(this);
        return this;
    }

    public Set<Machine> getMachines() {
        return machines;
    }

    public Company addMachine(Machine machine) {
        this.machines.add(machine);
        machine.setCompany(this);
        return this;
    }

    public Set<Person> getTechnicians() {
        return technicians;
    }

    public Company addTechnician(Person technician) {
        this.technicians.add(technician);
        technician.setCompany(this);
        return this;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getOwner() != null ? getOwner().hashCode() : 0);
        result = 31 * result + (getMachines() != null ? getMachines().hashCode() : 0);
        result = 31 * result + (getTechnicians() != null ? getTechnicians().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Company{" +
                "owner=" + owner +
                ", machines=" + machines +
                ", technicians=" + technicians +
                "} " + super.toString();
    }

    @PreRemove
    void preRemove() {
        if (owner != null) owner.setOwnedCompany(null);
    }
}
