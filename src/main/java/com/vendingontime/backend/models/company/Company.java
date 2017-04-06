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
        @NamedQuery(name = "Company.findById", query = "SELECT c FROM Company c WHERE c.id = :id")
})
public class Company extends AbstractEntity<Company> {

    @OneToOne(mappedBy = "company")
    private Person owner;

    @OneToMany(mappedBy = "company")
    private Set<Machine> machines = new HashSet<>();

    public Company() {
        super();
    }

    @Override
    public void update(Company company) {
        this.owner = company.getOwner();
        this.machines = company.getMachines();
    }

    public Person getOwner() {
        return owner;
    }

    public Company setOwner(Person owner) {
        this.owner = owner;
        owner.setCompany(this);
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
}
