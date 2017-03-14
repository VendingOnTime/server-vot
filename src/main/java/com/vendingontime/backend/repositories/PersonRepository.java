package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Optional;

/**
 * Created by miguel on 7/3/17.
 */
public class PersonRepository implements CRUDRepository<String, Person> {

    // FIXME: 13/03/2017 Make data source parameterizable
    private static EntityManager em = Persistence.createEntityManagerFactory("dataSource").createEntityManager();

    @Override
    public Person create(@NonNull Person person) throws PersonCollisionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            checkIfCollides(person);
            em.persist(person);
        } finally {
            tx.commit();
        }
        return person;
    }

    @Override
    public Optional<Person> findById(String id) {
        return findByQuery("findById", "id", id);
    }

    public Optional<Person> findByEmail(String email) {
        return findByQuery("findByEmail", "email", email);
    }

    public Optional<Person> findByUsername(String username) {
        return findByQuery("findByUsername", "username", username);
    }

    public Optional<Person> findByDni(String dni) {
        return findByQuery("findByDni", "dni", dni);
    }

    @Override
    public Optional<Person> update(@NonNull Person person) throws PersonCollisionException {
        EntityTransaction tr = em.getTransaction();
        tr.begin();

        Person found = em.find(Person.class, person.getId());
        if (found == null) {
            tr.commit();
            return Optional.empty();
        }

        try {
            checkIfCollides(person);
            found.update(person);
        } finally {
            tr.commit();
        }

        return Optional.of(found);
    }

    @Override
    public Optional<Person> delete(@NonNull String id) {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        Person found = em.find(Person.class, id);
        if (found == null) {
            tr.commit();
            return Optional.empty();
        }
        em.remove(found);
        tr.commit();
        return Optional.of(found);
    }

    private Optional<Person> findByQuery(String queryName, String paramName, Object param) {
        if (param == null) return Optional.empty();

        TypedQuery<Person> query = em.createNamedQuery("Person." + queryName, Person.class);
        query.setParameter(paramName, param);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private void checkIfCollides(Person person) throws PersonCollisionException {
        boolean isNew = person.getId() == null;

        Optional<Person> byEmail = findByEmail(person.getEmail());
        if (byEmail.isPresent() && (isNew || !byEmail.get().getId().equals(person.getId()))) {
            throw new PersonCollisionException(PersonCollisionException.Cause.EMAIL);
        }
        Optional<Person> byUsername = findByUsername(person.getUsername());
        if (byUsername.isPresent() && (isNew || !byUsername.get().getId().equals(person.getId()))) {
            throw new PersonCollisionException(PersonCollisionException.Cause.USERNAME);
        }
        Optional<Person> byDni = findByDni(person.getDni());
        if (byDni.isPresent() && (isNew || !byDni.get().getId().equals(person.getId()))) {
            throw new PersonCollisionException(PersonCollisionException.Cause.DNI);
        }
    }
}
