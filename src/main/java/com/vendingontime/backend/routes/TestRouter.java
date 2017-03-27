package com.vendingontime.backend.routes;

import spark.Service;

/**
 * Created by alberto on 27/3/17.
 */
public class TestRouter implements SparkRouter {

    @Override
    public void configure(Service http) {
        http.get("/api", (req, res) -> "Hello World");
    }
}
