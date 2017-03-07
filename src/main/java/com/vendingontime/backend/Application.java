package com.vendingontime.backend;

import static spark.Spark.*;

/**
 * Created by alberto on 7/3/17.
 */
public class Application {

    public static void main(String[] args) {
        String envPort = System.getenv("PORT");
        int port = Integer.parseInt(envPort != null ? envPort : "8080");
        port(port);

        get("/api", (req, res) -> "Hello World");
    }
}
