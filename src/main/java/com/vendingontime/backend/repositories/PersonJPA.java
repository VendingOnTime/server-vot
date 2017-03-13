package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.Person;

import javax.persistence.*;

/**
 * Created by miguel on 7/3/17.
 */
public class PersonJPA implements PersonStorage {
    private final static Person NOT_FOUND = new Person();
    private static EntityManager em = Persistence.createEntityManagerFactory("dataSource").createEntityManager();

    @Override
    public boolean create(Person person) {
        EntityTransaction tx = em.getTransaction();
        em.getTransaction().begin();
            em.persist(person);
        em.getTransaction().commit();
        return true;
    }

    @Override
    public Person retrieve(int id) {
        TypedQuery<Person> query = em.createNamedQuery("Person.findById", Person.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
//            return new Person("Not found", "", "");
            return NOT_FOUND;
        }
    }

    @Override
    public Person update(Person person) {
        Person found = em.find(Person.class, person.getId());
        em.getTransaction().begin();
//            found.update(person);
        em.getTransaction().commit();
        return found;
    }

    @Override
    public Person delete(int id) {
        Person found = em.find(Person.class, id);
        em.getTransaction().begin();
        em.remove(found);
        em.getTransaction().commit();
        return found;
    }
}
