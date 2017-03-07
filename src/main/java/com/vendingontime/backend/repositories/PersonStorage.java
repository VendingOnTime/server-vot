package com.vendingontime.backend.repositories;

import com.vendingontime.backend.models.Person;

/**
 * Created by miguel on 7/3/17.
 */
public interface PersonStorage {
    boolean create(Person person);
    Person retrieve(int id);
    Person update(Person person);
    Person delete(int id);
}
