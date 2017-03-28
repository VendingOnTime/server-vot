package com.vendingontime.backend.routes;

import spark.Service;

/**
 * Created by alberto on 27/3/17.
 */
public interface SparkRouter {
    void configure(Service http);
}
