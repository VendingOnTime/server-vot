package com.vendingontime.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vendingontime.backend.config.inject.ConfigModule;
import com.vendingontime.backend.initializers.RouteInitializer;

/**
 * Created by alberto on 7/3/17.
 */
public class Application {

    public static void main(String[] args) {
        new Application().start();
    }

    private void start() {
        Injector injector = Guice.createInjector(new ConfigModule());
        RouteInitializer initializer = injector.getInstance(RouteInitializer.class);
        initializer.setUp();
    }

    private void initialConfig() {
//        InitDB.generateSchemas();
//        MemoryServerConfig.config();
    }
}
