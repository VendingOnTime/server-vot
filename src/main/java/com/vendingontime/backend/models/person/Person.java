package com.vendingontime.backend.models.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vendingontime.backend.models.AbstractEntity;
import com.vendingontime.backend.models.bodymodels.person.EditPersonData;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.models.company.Company;

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
@NamedQueries({
        @NamedQuery(name = "Person.findById", query = "SELECT p FROM Person p WHERE p.id = :id AND p.disabled = false"),
        @NamedQuery(name = "Person.findByEmail", query = "SELECT p FROM Person p WHERE p.email = :email AND p.disabled = false"),
        @NamedQuery(name = "Person.findByUsername", query = "SELECT p FROM Person p WHERE p.username = :username AND p.disabled = false"),
        @NamedQuery(name = "Person.findByDni", query = "SELECT p FROM Person p WHERE p.dni = :dni AND p.disabled = false")
})
public class Person extends AbstractEntity<Person> {
    @Column(unique = true) private String email;
    @Column(unique = true) private String username;
    @Column @JsonIgnore private String password;
    @Column(unique = true) private String dni;
    @Column private String name;
    @Column private String surnames;
    @Column @Enumerated private PersonRole role;

    @OneToOne
    @JsonIgnore
    private Company company;

    public Person() {
        super();
    }

    public Person(SignUpData signUpData) {
        this.email = signUpData.getEmail();
        this.username = signUpData.getUsername();
        this.password = signUpData.getPassword();
        this.dni = signUpData.getDni();
        this.name = signUpData.getName();
        this.surnames = signUpData.getSurnames();
        this.role = signUpData.getRole();
    }

    public Person(EditPersonData editPersonData) {
        this((SignUpData) editPersonData);
        this.setId(editPersonData.getId());
    }

    public void update(Person person) {
        this.email = person.getEmail();
        this.username = person.getUsername();
        this.password = person.getPassword();
        this.dni = person.getDni();
        this.name = person.getName();
        this.surnames = person.getSurnames();
        this.role = person.getRole();
    }

    public String getEmail() {
        return email;
    }

    public Person setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Person setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Person setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDni() {
        return dni;
    }

    public Person setDni(String dni) {
        this.dni = dni;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurnames() {
        return surnames;
    }

    public Person setSurnames(String surnames) {
        this.surnames = surnames;
        return this;
    }

    public PersonRole getRole() {
        return role;
    }

    public Person setRole(PersonRole role) {
        this.role = role;
        return this;
    }

    public Company getCompany() {
        return company;
    }

    public Person setCompany(Company company) {
        this.company = company;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Person person = (Person) o;

        if (getEmail() != null ? !getEmail().equals(person.getEmail()) : person.getEmail() != null) return false;
        if (getUsername() != null ? !getUsername().equals(person.getUsername()) : person.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(person.getPassword()) : person.getPassword() != null)
            return false;
        if (getDni() != null ? !getDni().equals(person.getDni()) : person.getDni() != null) return false;
        if (getName() != null ? !getName().equals(person.getName()) : person.getName() != null) return false;
        if (getSurnames() != null ? !getSurnames().equals(person.getSurnames()) : person.getSurnames() != null)
            return false;
        return getRole() == person.getRole();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getDni() != null ? getDni().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getSurnames() != null ? getSurnames().hashCode() : 0);
        result = 31 * result + (getRole() != null ? getRole().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", surnames='" + surnames + '\'' +
                ", role=" + role +
                "} " + super.toString();
    }
}
