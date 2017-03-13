package com.vendingontime.backend;

import com.vendingontime.backend.config.ServerConfig;
import com.vendingontime.backend.initializers.InitDB;
import com.vendingontime.backend.models.Person;
import com.vendingontime.backend.repositories.PersonJPA;
import spark.servlet.SparkApplication;

import javax.persistence.Persistence;

import java.util.HashMap;

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
        ServerConfig.config();
    }

    private void generateRoutes() {
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
