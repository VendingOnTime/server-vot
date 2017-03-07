package com.vendingontime.backend.models;

import javax.persistence.*;

/**
 * Created by miguel on 7/3/17.
 */

@Entity
@NamedQueries({
        @NamedQuery(name = "Person.findById", query = "SELECT p FROM Person p WHERE p.id = :id")
})
public class Person {
    @Id
    private int id;
    private String name;

    public Person() {
        super();
    }

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void update(Person person) {
        name = person.name;
    }
}
