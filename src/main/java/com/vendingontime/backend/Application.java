package com.vendingontime.backend;

import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.repositories.PersonJPA;

import javax.persistence.Persistence;

import java.util.HashMap;

import static spark.Spark.*;

/**
 * Created by alberto on 7/3/17.
 */
public class Application {

    public static void main(String[] args) {
        Persistence.generateSchema("dataSource", new HashMap());

        String envPort = System.getenv("PORT");
        int port = Integer.parseInt(envPort != null ? envPort : "8080");
        port(port);

        get("/api", (req, res) -> "Hello World");
        post("/person/create", (req, res) -> {
            Person person = new Person(1, "Alberto");
            PersonJPA pJPA = new PersonJPA();

            if (pJPA.create(person)) {
                return person.getName() + " (" + person.getId() + ") created";
            } else {
                return "Could not create new person";
            }

        });
    }
}
