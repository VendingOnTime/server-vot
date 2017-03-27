package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.models.PersonCollisionException;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.vendingontime.backend.models.PersonCollisionException.Cause.*;
import static com.vendingontime.backend.models.PersonCollisionException.Cause;

/**
 * Created by miguel on 7/3/17.
 */
public class PersonRepository implements CRUDRepository<String, Person> {

    // FIXME: 13/03/2017 Make data source parameterizable
    private static EntityManager em = Persistence.createEntityManagerFactory("derby_in_memory").createEntityManager();

    @Override
    public Person create(Person person) throws PersonCollisionException {
        if (person == null) throw new NullPointerException("person");
        if (person.getId() != null) return person;
        checkIfCollides(person);

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.persist(person);
        tx.commit();

        em.detach(person);
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
    public Optional<Person> update(Person person) throws PersonCollisionException {
        if (person == null) throw new NullPointerException("person");

        Optional<Person> possiblePerson = findById(person.getId());
        possiblePerson.ifPresent(found -> {
            try {
                checkIfCollides(person);

                EntityTransaction tr = em.getTransaction();
                tr.begin();
                found.update(person);
                tr.commit();
            } finally {
                em.detach(found);
            }
        });

        return possiblePerson.isPresent() ? possiblePerson : Optional.empty();
    }

    @Override
    public Optional<Person> delete(String id) {
        if (id == null) throw new NullPointerException("id");

        Optional<Person> possiblePerson = findById(id);
        possiblePerson.ifPresent(found -> {
            EntityTransaction tr = em.getTransaction();
            tr.begin();
            em.remove(found);
            tr.commit();
        });

        return possiblePerson.isPresent() ? possiblePerson : Optional.empty();
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
