package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.Person;

import javax.persistence.*;
import java.util.Optional;

/**
 * Created by miguel on 7/3/17.
 */
public class PersonRepository implements CRUDRepository<String, Person> {

    // FIXME: 13/03/2017 Make data source parameterizable
    private static EntityManager em = Persistence.createEntityManagerFactory("dataSource").createEntityManager();

    @Override
    public Person create(Person person) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(person);
        tx.commit();
        return person;
    }

    @Override
    public Optional<Person> retrieve(String id) {
        TypedQuery<Person> query = em.createNamedQuery("Person.findById", Person.class);
        query.setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Person> update(Person person) {
        Person found = em.find(Person.class, person.getId());
        if (found == null) {
            return Optional.empty();
        }

        EntityTransaction tr = em.getTransaction();
        tr.begin();
        found.update(person);
        tr.commit();
        return Optional.of(found);
    }

    @Override
    public Optional<Person> delete(String id) {
        Person found = em.find(Person.class, id);
        if (found == null) {
            return Optional.empty();
        }
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.remove(found);
        tr.commit();
        return Optional.of(found);
    }
}
