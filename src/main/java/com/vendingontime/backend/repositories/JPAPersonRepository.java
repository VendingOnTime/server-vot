package com.vendingontime.backend.repositories;

import com.google.inject.Inject;
import com.vendingontime.backend.models.person.Person;
import com.vendingontime.backend.models.person.PersonCollisionException;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.vendingontime.backend.models.person.PersonCollisionException.Cause.*;
import static com.vendingontime.backend.models.person.PersonCollisionException.Cause;

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
public class JPAPersonRepository extends JPARepository<Person> implements PersonRepository {

    @Inject
    public JPAPersonRepository(EntityManager entityManager) {
        super(entityManager, Person.class);
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return findByQuery("findByEmail", "email", email);
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        return findByQuery("findByUsername", "username", username);
    }

    @Override
    public Optional<Person> findByDni(String dni) {
        return findByQuery("findByDni", "dni", dni);
    }

    protected void checkIfCollides(Person person) throws PersonCollisionException {
        boolean isNew = person.getId() == null;

        List<Cause> causes = new LinkedList<>();

        Optional<Person> byEmail = findByEmail(person.getEmail());
        if (byEmail.isPresent() && (isNew || !byEmail.get().getId().equals(person.getId()))) {
            causes.add(EMAIL);
        }
        Optional<Person> byUsername = findByUsername(person.getUsername());
        if (byUsername.isPresent() && (isNew || !byUsername.get().getId().equals(person.getId()))) {
            causes.add(USERNAME);
        }
        Optional<Person> byDni = findByDni(person.getDni());
        if (byDni.isPresent() && (isNew || !byDni.get().getId().equals(person.getId()))) {
            causes.add(DNI);
        }

        if (causes.size() > 0) throw new PersonCollisionException(causes.toArray(new Cause[causes.size()]));
    }
}
