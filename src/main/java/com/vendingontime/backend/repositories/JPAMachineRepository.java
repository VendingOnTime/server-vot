package com.vendingontime.backend.repositories;/*
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

import com.google.inject.Inject;
import com.vendingontime.backend.models.machine.Machine;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

public class JPAMachineRepository implements MachineRepository {
    private final EntityManager em;

    @Inject
    public JPAMachineRepository(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public Machine create(Machine machine) {
        if (machine == null) throw new NullPointerException("machine");
        if (machine.getId() != null) return machine;

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.persist(machine);
        tx.commit();

        em.detach(machine);
        return machine;
    }

    @Override
    public Optional<Machine> findById(String id) {
        return findByQuery("findById", "id", id);
    }

    @Override
    public Optional<Machine> update(Machine machine) {
        if (machine == null) throw new NullPointerException("machine");

        Optional<Machine> possibleMachine = findById(machine.getId());
        possibleMachine.ifPresent(found -> {
            try {
                EntityTransaction tr = em.getTransaction();
                tr.begin();
                found.update(machine);
                tr.commit();
            } finally {
                em.detach(found);
            }
        });

        return possibleMachine;
    }

    @Override
    public Optional<Machine> delete(String id) {
        if (id == null) throw new NullPointerException("id");

        Optional<Machine> possibleMachine = findById(id);
        possibleMachine.ifPresent(found -> {
            EntityTransaction tr = em.getTransaction();
            tr.begin();
            em.remove(found);
            tr.commit();
        });

        return possibleMachine;
    }

    private Optional<Machine> findByQuery(String queryName, String paramName, Object param) {
        if (param == null) return Optional.empty();

        TypedQuery<Machine> query = em.createNamedQuery("Machine." + queryName, Machine.class);
        query.setParameter(paramName, param);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
