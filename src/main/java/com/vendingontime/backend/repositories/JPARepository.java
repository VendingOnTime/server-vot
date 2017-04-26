package com.vendingontime.backend.repositories;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class JPARepository<MODEL extends AbstractEntity> implements Repository<MODEL> {


    protected final EntityManager em;
    private final Class<MODEL> entityClass;

    protected JPARepository(EntityManager em, Class<MODEL> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    @Override
    public MODEL create(MODEL model) throws RuntimeException {
        checkModelNullity(model);
        if (model.getId() != null) return model;
        checkIfCollides(model);

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.persist(model);
        tx.commit();

        em.detach(model);
        return model;
    }

    @Override
    public Optional<MODEL> findById(String id) {
        return findOneBy("findById", "id", id);
    }

    @Override
    public Optional<MODEL> update(MODEL model) throws RuntimeException {
        checkModelNullity(model);

        Optional<MODEL> possibleModel = findById(model.getId());
        possibleModel.ifPresent(found -> {
            try {
                checkIfCollides(model);

                EntityTransaction tr = em.getTransaction();
                tr.begin();
                found.update(model);
                tr.commit();
            } finally {
                em.detach(found);
            }
        });

        return possibleModel;
    }

    @Override
    public Optional<MODEL> delete(String id) throws RuntimeException {
        if (id == null) throw new NullPointerException("id");

        Optional<MODEL> possibleModel = findById(id);
        possibleModel.ifPresent(found -> {
            EntityTransaction tr = em.getTransaction();
            tr.begin();
            em.remove(found);
            tr.commit();
        });

        return possibleModel;
    }

    // FIXME: 18/4/17 Add a warning to avoid using this method in production
    @Override
    public void deleteAll() {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
        tr.commit();
    }

    private void checkModelNullity(MODEL model) {
        if (model == null) throw new NullPointerException(entityClass.getSimpleName().toLowerCase());
    }

    /**
     * Override this method to perform entity collision checks before create() and update()
     * @param model
     */
    protected void checkIfCollides(MODEL model) throws RuntimeException {}

    protected Optional<MODEL> findOneBy(String queryName, String paramName, Object param) {
        if (param == null) return Optional.empty();

        TypedQuery<MODEL> query = buildQuery(queryName);
        query.setParameter(paramName, param);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    protected List<MODEL> findManyBy(String queryName, String paramName, Object param) {
        if (param == null) return new LinkedList<MODEL>();

        TypedQuery<MODEL> query = buildQuery(queryName);
        query.setParameter(paramName, param);

        return query.getResultList();
    }

    protected TypedQuery<MODEL> buildQuery(String queryName) {
        return em.createNamedQuery(entityClass.getSimpleName() + "." + queryName, entityClass);
    }
}
