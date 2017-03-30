package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.person.Person;

import java.util.Optional;

/**
 * Created by alberto on 28/3/17.
 */
public interface PersonRepository extends Repository<String, Person> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByUsername(String email);
    Optional<Person> findByDni(String email);
}
