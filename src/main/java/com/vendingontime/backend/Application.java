package com.vendingontime.backend;

import com.vendingontime.backend.config.MemoryServerConfig;
import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.repositories.PersonRepository;

import static spark.Spark.*;

/**
 * Created by alberto on 7/3/17.
 */
public class Application {

    public static void main(String[] args) {
        Application app = new Application();
        app.initialConfig();

        app.generateRoutes();
    }

    private void initialConfig() {
        InitDB.generateSchemas();
//        MemoryServerConfig.config();
    }

    private void generateRoutes() {
        get("/api", (req, res) -> "Hello World");
        post("/person/create", (req, res) -> {
            Person person = new Person();
            PersonRepository pJPA = new PersonRepository();

            if (pJPA.create(person) != null) {
                return person.getName() + " (" + person.getId() + ") created";
            } else {
                return "Could not create new person";
            }
        });
    }
}
